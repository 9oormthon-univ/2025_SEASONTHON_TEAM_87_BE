package com.developing.bluffing.auth.facade.impl;

import com.developing.bluffing.auth.dto.request.LocalLoginRequest;
import com.developing.bluffing.auth.dto.request.LocalRegisterRequest;
import com.developing.bluffing.auth.dto.response.AllTokenResponse;
import com.developing.bluffing.auth.facade.LocalAuthFacade;
import com.developing.bluffing.security.exception.CustomJwtException;
import com.developing.bluffing.security.exception.errorCode.CustomJwtErrorCode;
import com.developing.bluffing.security.service.AccessTokenBlacklistService;
import com.developing.bluffing.security.service.RefreshTokenService;
import com.developing.bluffing.security.util.JwtUtil;
import com.developing.bluffing.user.convertor.UserFactory;
import com.developing.bluffing.user.entity.Users;
import com.developing.bluffing.user.exception.UserException;
import com.developing.bluffing.user.exception.errorCode.UserErrorCode;
import com.developing.bluffing.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalAuthFacadeImpl implements LocalAuthFacade {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final AccessTokenBlacklistService tokenBlacklistService;
    private final JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder;

    //로그인
    @Transactional(readOnly = false)
    @Override
    public AllTokenResponse login(LocalLoginRequest request) {
        // 사용자 조회
        Users user = userService.getByLoginId(request.getLoginId());

        // 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())
                && user.getLoginId().equals(request.getLoginId())) {
            log.warn("[Auth] 잘못된 로그인 inputId -> {} \t inputPwd -> {}"
                    ,request.getLoginId(),request.getPassword() );
            throw new UserException(UserErrorCode.INVALID_USER_ERROR);
        }

        // 성공시 생성 및 반환
        String access = jwtUtil.createAccessToken(user);
        String refresh = jwtUtil.createRefreshToken(user);
        refreshTokenService.createOrUpdateRefreshToken(user,refresh);
        return AllTokenResponse.builder().accessToken(access).refreshToken(refresh).build();
    }

    //회원가입
    @Transactional(readOnly = false)
    @Override
    public AllTokenResponse register(LocalRegisterRequest request) {
        //한번 더 검증
        if (userService.isExistByLoginId(request.getLoginId())){
            throw new UserException(UserErrorCode.USER_LOGIN_ID_DUPLICATED_ERROR);
        }
        //유저 생성 및 저장
        Users newUser = UserFactory.toUser(request,passwordEncoder);
        Users savedUSer = userService.saveOrThrow(newUser);

        String access = jwtUtil.createAccessToken(savedUSer);
        String refresh = jwtUtil.createRefreshToken(savedUSer);
        refreshTokenService.createOrUpdateRefreshToken(savedUSer,refresh);
        return AllTokenResponse.builder().accessToken(access).refreshToken(refresh).build();
    }

    //AccessToken 재발급
    @Transactional(readOnly = false)
    @Override
    public AllTokenResponse accessTokenReissue(String refreshToken) {
        // 1. RefreshToken 유효성 검증
        if(!jwtUtil.validateRefreshToken(refreshToken)){
            throw new CustomJwtException(CustomJwtErrorCode.JWT_INVALID_ERROR);
        }

        // 2. RefreshToken에서 유저 정보 추출
        UUID isExistUserId = jwtUtil.getSubjectFromRefreshToken(refreshToken);

        // 3. 사용자 조회
        Users user = userService.getById(isExistUserId);

        // 4. DB에 저장된 RefreshToken 조회
        String savedRefreshToken = refreshTokenService.getByUser(user).getRefreshToken();

        // 5. DB의 RefreshToken과 요청값 비교
        if (!savedRefreshToken.equals(refreshToken)) {
            log.warn("[Token] RefreshToken 불일치 - DB와 요청값이 다름");
            throw new CustomJwtException(CustomJwtErrorCode.JWT_INVALID_ERROR);
        }

        // 6. AccessToken 새로 발급
        String newAccessToken = jwtUtil.createAccessToken(user);
        String newRefreshToken = refreshToken;

        // 7. RefreshToken 만료 임박시 새로 발급
        long remaining =
                jwtUtil.getExpirationFromRefreshToken(newRefreshToken).getTime() - System.currentTimeMillis();
        if (remaining < TimeUnit.DAYS.toMillis(3)) {
            newRefreshToken = jwtUtil.createRefreshToken(user);
            refreshTokenService.createOrUpdateRefreshToken(user, newRefreshToken);
            log.info("[JWT] RefreshToken 갱신완료 사유 : 만료 임박");
        }
        log.info("[JWT] AccessToken 재발급 완료 - userId: {}", user.getId());

        return AllTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

}
