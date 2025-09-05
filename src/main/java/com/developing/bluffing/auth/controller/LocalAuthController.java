package com.developing.bluffing.auth.controller;

import com.developing.bluffing.auth.dto.request.LocalLoginRequest;
import com.developing.bluffing.auth.dto.request.LocalRegisterRequest;
import com.developing.bluffing.auth.dto.response.AllTokenResponse;
import com.developing.bluffing.auth.facade.LocalAuthFacade;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/local")
public class LocalAuthController {

    private final LocalAuthFacade localAuthFacade;

    @PostMapping("/login")
    public ResponseEntity<AllTokenResponse> login(@RequestBody @Valid LocalLoginRequest request) {
        AllTokenResponse tokens = localAuthFacade.login(request);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/register")
    public ResponseEntity<AllTokenResponse> register(@RequestBody @Valid LocalRegisterRequest request) {
        AllTokenResponse tokens = localAuthFacade.register(request);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AllTokenResponse> refresh(@RequestBody @Valid RefreshTokenRequest request) {
        AllTokenResponse tokens = localAuthFacade.accessTokenReissue(request.refreshToken());
        return ResponseEntity.ok(tokens);
    }

    /**
     * 간단 DTO (필요시 별도 파일로 분리)
     */
    public record RefreshTokenRequest(
            @NotBlank(message = "리프레시 토큰은 비어있을 수 없습니다.")
            String refreshToken
    ) {}
}