package com.developing.bluffing.game.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameRecord {
    private Long userGameCount;
    private Long userWinCount;
    private Long userLossCount;
}
