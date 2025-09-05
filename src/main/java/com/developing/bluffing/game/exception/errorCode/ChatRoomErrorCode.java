package com.developing.bluffing.game.exception.errorCode;

import com.developing.bluffing.global.exception.GlobalException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ChatRoomErrorCode implements GlobalException {
    CHAT_ROOM_NOT_FOUND_ERROR(HttpStatus.BAD_REQUEST,8000,"해당 채팅을 찾지 못하였습니다."),
    CHAT_ROOM_CREATE_ERROR(HttpStatus.BAD_REQUEST,8001,"채팅방 저장에 실패하였습니다.");

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
