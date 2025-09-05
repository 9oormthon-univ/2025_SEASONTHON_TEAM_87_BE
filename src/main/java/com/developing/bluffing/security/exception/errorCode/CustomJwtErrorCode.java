package com.developing.bluffing.security.exception.errorCode;

import com.developing.bluffing.global.exception.GlobalException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum CustomJwtErrorCode implements GlobalException {

    JWT_CREATE_ERROR(HttpStatus.BAD_REQUEST,9000,"JWT토큰 생성 실패"),
    JWT_INVALID_ERROR(HttpStatus.BAD_REQUEST,9010,"JWT토큰 검증 실패");


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
