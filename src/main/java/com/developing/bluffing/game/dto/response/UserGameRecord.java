package com.developing.bluffing.game.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class UserGameRecord {
    private UUID userId;
    private Long userGameCount;
    private Long userWinCount;
    private Long userLossCount;
    private LocalDateTime searchTime;
}
