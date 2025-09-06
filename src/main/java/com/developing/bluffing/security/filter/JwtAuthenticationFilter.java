package com.developing.bluffing.security.filter;

import com.developing.bluffing.security.service.impl.UserDetailImplServiceImpl;
import com.developing.bluffing.security.service.AccessTokenBlacklistService;
import com.developing.bluffing.security.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailImplServiceImpl userDetailsService;
    private final AccessTokenBlacklistService accessTokenBlacklistService;

    public JwtAuthenticationFilter(
            JwtUtil jwtUtil,
            UserDetailImplServiceImpl userDetailsService,
            AccessTokenBlacklistService accessTokenBlacklistService
    ) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.accessTokenBlacklistService = accessTokenBlacklistService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return path.startsWith("/api/v1/auth/")
                || path.equals("/api/v1/user/id")
                || path.startsWith("/ws/")
                || path.startsWith("/api/v1/auth/local");

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = jwtUtil.resolveTokenFromHttpServletRequest(request);

        if (token != null && jwtUtil.validateAccessToken(token)) {
            try {
                Claims claims = jwtUtil.getClaimsFromAccessToken(token);
                String stringJti = claims.getId();
                UUID uuidJti = jwtUtil.getJtiFromAccessToken(token);
                UUID userId = jwtUtil.getSubjectFromAccessToken(token);

                //블랙리스트 체크
                if (accessTokenBlacklistService.isBlackListed(uuidJti)) {
                    log.warn("[JWT] 블랙리스트 토큰 감지 - jti: {}", stringJti);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그아웃된 토큰입니다.");
                    return;
                }

                // ✅ 유저 정보 로드
                UserDetails userDetails = userDetailsService.loadUserByUserId(userId);
                log.debug("[JWT] UserDetails 로드 성공 - userId: {}", userId);


                // ✅ 인증 객체 생성 및 SecurityContext 설정
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info("[JWT] 인증 완료 - SecurityContext에 유저 등록됨  userId : {}", userId);
            } catch (Exception e) {
                log.error("[JWT] 토큰 처리 중 예외 발생: {}", e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다.");
                return;
            }
        } else {
            if (token != null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다.");
                log.warn("[JWT] 토큰 유효성 검사 실패 또는 만료됨");
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다.");
                log.debug("[JWT] Authorization 헤더에 토큰 없음");
            }
        }

        filterChain.doFilter(request, response);
    }
}

