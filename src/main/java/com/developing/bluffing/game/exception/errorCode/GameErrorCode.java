package com.developing.bluffing.game.exception.errorCode;

import com.developing.bluffing.global.exception.GlobalException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GameErrorCode implements GlobalException {
    GAME_NOT_NOW_PHASE(HttpStatus.BAD_REQUEST,7000,"현재 요청과 게임 상황이 맞지 않습니다."),
    GAME_CANT_VOTE_SELF(HttpStatus.BAD_REQUEST,7001,"자기 자신에게 투표할 수 없습니다."),
    GAME_DATA_INCONSISTENCY(HttpStatus.INTERNAL_SERVER_ERROR,7002,"게임 데이터가 불일치합니다.");

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
