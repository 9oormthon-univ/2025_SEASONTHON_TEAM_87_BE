package com.developing.bluffing.game.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class GameReadyRequest {

    @NotNull(message = "chatRoomId는 필수입니다.")
    private UUID chatRoomId;
}