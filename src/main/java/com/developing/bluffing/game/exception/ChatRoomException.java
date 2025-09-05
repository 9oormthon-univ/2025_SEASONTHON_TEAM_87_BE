package com.developing.bluffing.game.exception;

import com.developing.bluffing.game.exception.errorCode.ChatRoomErrorCode;
import com.developing.bluffing.global.exception.GlobalBaseException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRoomException extends GlobalBaseException {

    private ChatRoomErrorCode errorCode;

}
