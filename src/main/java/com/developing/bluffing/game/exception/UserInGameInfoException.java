package com.developing.bluffing.game.exception;

import com.developing.bluffing.game.exception.errorCode.UserInGameInfoErrorCode;
import com.developing.bluffing.global.exception.GlobalBaseException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInGameInfoException extends GlobalBaseException {
    private UserInGameInfoErrorCode errorCode;
}
