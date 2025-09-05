package com.developing.bluffing.user.exception;

import com.developing.bluffing.global.exception.GlobalBaseException;
import com.developing.bluffing.user.exception.errorCode.UserErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserException extends GlobalBaseException {
    private UserErrorCode errorCode;
}
