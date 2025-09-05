package com.developing.bluffing.security.util;

import com.developing.bluffing.security.exception.CustomJwtException;
import com.developing.bluffing.security.exception.errorCode.CustomJwtErrorCode;
import com.developing.bluffing.user.entity.Users;
import com.github.f4b6a3.uuid.UuidCreator;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    // ---- application.yaml 주입 ----
    @Value("${jwt.issuer:saferoad}")
    private String issuer;

    @Value("${jwt.access.secret}")
    private String accessSecretBase64;   // Base64 인코딩된 랜덤 바이트

    @Value("${jwt.refresh.secret}")
    private String refreshSecretBase64;  // Base64 인코딩된 랜덤 바이트

    @Value("${jwt.access.ttl}")
    private Duration accessTtl;

    @Value("${jwt.refresh.ttl}")
    private Duration refreshTtl;

    // ---- 내부 상태 ----
    private Key accessKey;   // HS256
    private Key refreshKey;  // HS256

    // 테스트/운영 모두에서 시간 제어 용이
    private final Clock clock = Clock.systemUTC();

    @PostConstruct
    void init() {
        this.accessKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessSecretBase64));
        this.refreshKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshSecretBase64));
    }

    // ====== 생성 ======
    public String createAccessToken(Users user) {
        return createToken(String.valueOf(user.getId()), accessKey, accessTtl);
    }

    public String createRefreshToken(Users user) {
        return createToken(String.valueOf(user.getId()), refreshKey, refreshTtl);
    }

    // ====== Claims ======
    public Claims getClaimsFromAccessToken(String token) {
        return parse(token, accessKey);
    }

    public Claims getClaimsFromRefreshToken(String token) {
        return parse(token, refreshKey);
    }

    // ====== subject ======
    public UUID getSubjectFromAccessToken(String token) {
        try{
            return UUID.fromString(getClaimsFromAccessToken(token).getSubject());
        }catch (IllegalArgumentException e){
            log.warn("[JWT] 잘못된 형식의 토큰  {}",e.getMessage());
            throw new CustomJwtException(CustomJwtErrorCode.JWT_INVALID_ERROR);
        }
    }

    public UUID getSubjectFromRefreshToken(String token) {
        try{
            return UUID.fromString(getClaimsFromRefreshToken(token).getSubject());
        }catch (IllegalArgumentException e){
            log.warn("[JWT] 잘못된 형식의 토큰  {}",e.getMessage());
            throw new CustomJwtException(CustomJwtErrorCode.JWT_INVALID_ERROR);
        }
    }

    // ====== jti ======
    public UUID getJtiFromAccessToken(String token) {
        try{
            return UUID.fromString(getClaimsFromAccessToken(token).getId());
        }catch (IllegalArgumentException e){
            log.warn("[JWT] 잘못된 형식의 토큰  {}",e.getMessage());
            throw new CustomJwtException(CustomJwtErrorCode.JWT_INVALID_ERROR);
        }
    }

    public UUID getJtiFromRefreshToken(String token) {
        try{
            return UUID.fromString(getClaimsFromRefreshToken(token).getId());
        }catch (IllegalArgumentException e){
            log.warn("[JWT] 잘못된 형식의 토큰  {}",e.getMessage());
            throw new CustomJwtException(CustomJwtErrorCode.JWT_INVALID_ERROR);
        }
    }

    // ====== expiration ======
    public Date getExpirationFromAccessToken(String token) {
        return getClaimsFromAccessToken(token).getExpiration();
    }

    public Date getExpirationFromRefreshToken(String token) {
        return getClaimsFromRefreshToken(token).getExpiration();
    }

    // ====== 검증(간단: boolean) ======
    public boolean validateAccessToken(String token) {
        return validateInternal(token, accessKey).isValid();
    }

    public boolean validateRefreshToken(String token) {
        return validateInternal(token, refreshKey).isValid();
    }

    // ====== 검증(자세히: 사유 포함) ======
    public TokenValidationResult validateAccessTokenDetailed(String token) {
        return validateInternal(token, accessKey);
    }

    public TokenValidationResult validateRefreshTokenDetailed(String token) {
        return validateInternal(token, refreshKey);
    }

    public String resolveTokenFromHttpServletRequest(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7).trim();
        }
        return null;
    }

    // ====== 내부 유틸 ======

    private String createToken(String subject, Key key, Duration ttl) {
        Instant now = clock.instant();
        Instant exp = now.plus(ttl);

        return Jwts.builder()
                .setSubject(subject)                 // PK만 담음
                .setId(generateJti())               // jti
                .setIssuer(issuer)                  // iss
                .setIssuedAt(Date.from(now))        // iat
                .setExpiration(Date.from(exp))      // exp
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims parse(String rawToken, Key key) {
        String token = stripBearerPrefix(rawToken);

        JwtParser parser = Jwts.parserBuilder()
                .requireIssuer(issuer)
                .setClock(() -> Date.from(clock.instant()))
                .setAllowedClockSkewSeconds(60)   // 시계 오차 허용 (필요시 조정)
                .setSigningKey(key)
                .build();

        return parser.parseClaimsJws(token).getBody();
    }

    private TokenValidationResult validateInternal(String rawToken, Key key) {
        String token = stripBearerPrefix(rawToken);
        try {
            JwtParser parser = Jwts.parserBuilder()
                    .requireIssuer(issuer)
                    .setClock(() -> Date.from(clock.instant()))
                    .setAllowedClockSkewSeconds(60)
                    .setSigningKey(key)
                    .build();

            parser.parseClaimsJws(token); // 성공하면 유효
            return TokenValidationResult.valid();
        } catch (ExpiredJwtException e) {
            return TokenValidationResult.invalid(TokenInvalidReason.EXPIRED, e.getMessage());
        } catch (UnsupportedJwtException e) {
            return TokenValidationResult.invalid(TokenInvalidReason.UNSUPPORTED, e.getMessage());
        } catch (MalformedJwtException e) {
            return TokenValidationResult.invalid(TokenInvalidReason.MALFORMED, e.getMessage());
        } catch (SecurityException | SignatureException e) {
            return TokenValidationResult.invalid(TokenInvalidReason.BAD_SIGNATURE, e.getMessage());
        } catch (IllegalArgumentException e) {
            return TokenValidationResult.invalid(TokenInvalidReason.ILLEGAL_ARGUMENT, e.getMessage());
        } catch (JwtException e) {
            return TokenValidationResult.invalid(TokenInvalidReason.OTHER, e.getMessage());
        }
    }

    private String stripBearerPrefix(String token) {
        if (token == null) return null;
        String t = token.trim();
        if (t.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return t.substring(7).trim();
        }
        return t;
    }

    private String generateJti() {
        return UuidCreator.getTimeOrderedEpoch().toString();
    }

    // 필요하면 테스트용으로 키 접근
    Key getAccessKey() {
        return accessKey;
    }

    Key getRefreshKey() {
        return refreshKey;
    }

    // ====== 검증 결과 타입 ======
    @Getter
    public static final class TokenValidationResult {
        private final boolean valid;
        private final TokenInvalidReason reason;
        private final String message;

        private TokenValidationResult(boolean valid, TokenInvalidReason reason, String message) {
            this.valid = valid;
            this.reason = reason;
            this.message = message;
        }

        public static TokenValidationResult valid() {
            return new TokenValidationResult(true, null, null);
        }

        public static TokenValidationResult invalid(TokenInvalidReason reason, String message) {
            return new TokenValidationResult(false, reason, message);
        }
    }

    public enum TokenInvalidReason {
        EXPIRED,
        BAD_SIGNATURE,
        MALFORMED,
        UNSUPPORTED,
        ILLEGAL_ARGUMENT,
        OTHER
    }
}