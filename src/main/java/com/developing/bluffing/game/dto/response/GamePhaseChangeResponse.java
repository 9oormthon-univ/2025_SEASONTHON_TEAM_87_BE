package com.developing.bluffing.game.dto.response;

import com.developing.bluffing.game.dto.enums.MessageReference;
import com.developing.bluffing.game.entity.enums.GamePhase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class GamePhaseChangeResponse {

    private UUID roomId;
    private String content;
    private GamePhase phase;
    private LocalDateTime changeTime;
    private MessageReference messageReference;

}
