package com.developing.bluffing.security.exception;

import com.developing.bluffing.global.exception.GlobalBaseException;
import com.developing.bluffing.security.exception.errorCode.CustomJwtErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomJwtException extends GlobalBaseException {
    private CustomJwtErrorCode errorCode;
}
