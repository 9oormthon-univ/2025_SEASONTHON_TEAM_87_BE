package com.developing.bluffing.game.exception.errorCode;

import com.developing.bluffing.global.exception.GlobalException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserInGameInfoErrorCode implements GlobalException {
    USER_IN_GAME_INFO_NOT_FOUND_ERROR(HttpStatus.BAD_REQUEST,9000,"게임 내 유저 정보를 찾을 수 없습니다."),
    USER_IN_GAME_INFO_CREATE_ERROR(HttpStatus.BAD_REQUEST,9001,"게임 유저 정보 저장에 실패하였습니다.");

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
