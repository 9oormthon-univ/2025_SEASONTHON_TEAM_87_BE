package com.developing.bluffing.security.service.impl;

import com.developing.bluffing.security.entity.RefreshToken;
import com.developing.bluffing.security.exception.RefreshTokenException;
import com.developing.bluffing.security.exception.errorCode.RefreshTokenErrorCode;
import com.developing.bluffing.security.repository.RefreshTokenRepository;
import com.developing.bluffing.security.service.RefreshTokenService;
import com.developing.bluffing.security.util.JwtUtil;
import com.developing.bluffing.user.entity.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final JwtUtil jwtUtil;

    @Override
    public RefreshToken createOrUpdateRefreshToken(Users user, String refreshToken) {
        RefreshToken entity = repository.findByUser(user)
                .orElse( RefreshToken.builder()
                        .refreshToken(refreshToken)
                        .user(user)
                        .expiredAt(jwtUtil.getExpirationFromRefreshToken(refreshToken))
                        .build());
        entity.updateRefreshToken(refreshToken,jwtUtil.getExpirationFromRefreshToken(refreshToken));
        return repository.save(entity);
    }

    @Override
    public RefreshToken getByUser(Users user) {
        return repository.findByUser(user)
                .orElseThrow( () -> new RefreshTokenException(RefreshTokenErrorCode.REFRESH_TOKEN_NOT_FOUND_ERROR));
    }
}
