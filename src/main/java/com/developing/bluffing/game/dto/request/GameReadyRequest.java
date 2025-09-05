package com.developing.bluffing.game.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class GameReadyRequest {

    private UUID chatRoomId;

}
