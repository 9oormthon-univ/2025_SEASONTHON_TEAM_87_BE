package com.developing.bluffing.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LocalLoginRequest {

    @NotBlank(message = "로그인 아이디는 비어있을 수 없습니다.")
    private String loginId;

    @NotBlank(message = "비밀번호는 비어있을 수 없습니다.")
    private String password;
}
