package com.developing.bluffing.security.exception;

import com.developing.bluffing.global.exception.GlobalBaseException;
import com.developing.bluffing.security.exception.errorCode.RefreshTokenErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshTokenException extends GlobalBaseException {

    private RefreshTokenErrorCode errorCode;

}
