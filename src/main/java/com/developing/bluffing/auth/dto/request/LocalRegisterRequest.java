package com.developing.bluffing.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class LocalRegisterRequest{

    @NotBlank(message = "로그인 아이디는 비어있을 수 없습니다.")
    private String loginId;

    @NotBlank(message = "비밀번호는 비어있을 수 없습니다.")
    private String password;

    @NotBlank(message = "이름은 비어있을 수 없습니다.")
    private String name;

    /**
    *  FIX : LocalDate로 받기 나중에 수정
    */
    @NotBlank(message = "생년월일은 비어있을 수 없습니다.")
    @Pattern(
            regexp = "^(19|20)\\d{2}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$",
            message = "생년월일 형식은 yyyy-MM-dd 이어야 합니다."
    )
    private String birth;
}

