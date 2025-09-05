package com.developing.bluffing.user.exception.errorCode;

import com.developing.bluffing.global.exception.GlobalException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum UserErrorCode implements GlobalException {

    USER_CREATE_ERROR(HttpStatus.BAD_REQUEST,2000,"유저 생성 오류입니다."),
    USER_NOT_FOUND_ERROR(HttpStatus.BAD_REQUEST,2010,"존재하지 않은 유저입니다."),
    USER_PERMISSION_ERROR(HttpStatus.UNAUTHORIZED,2020,"권한이 없습니다."),
    INVALID_USER_ERROR(HttpStatus.BAD_REQUEST,2030,"잘못된 아이디 혹은 비밀번호 입니다."),
    USER_LOGIN_ID_DUPLICATED_ERROR(HttpStatus.BAD_REQUEST,2040,"이미 존재하는 ID입니다.");

    private final HttpStatus status;
    private final int errorCode;
    private final String message;

    @Override
    public int getStatus() {
        return status.value();
    }

    @Override
    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
