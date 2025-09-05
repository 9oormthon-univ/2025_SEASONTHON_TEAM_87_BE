package com.developing.bluffing.security.exception.errorCode;

import com.developing.bluffing.global.exception.GlobalException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RefreshTokenErrorCode implements GlobalException {
    REFRESH_TOKEN_NOT_FOUND_ERROR(HttpStatus.BAD_REQUEST,6000,"존재하지 않는 토큰입니다."),
    REFRESH_TOKEN_CREATE_ERROR(HttpStatus.BAD_REQUEST,6010,"리프레시 토큰 저장 실패");


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
