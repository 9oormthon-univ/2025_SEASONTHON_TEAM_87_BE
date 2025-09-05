package com.developing.bluffing.game.exception;

import com.developing.bluffing.game.exception.errorCode.GameErrorCode;
import com.developing.bluffing.global.exception.GlobalBaseException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameException extends GlobalBaseException {
    private GameErrorCode errorCode;
}
