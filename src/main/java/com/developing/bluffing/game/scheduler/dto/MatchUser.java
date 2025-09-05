package com.developing.bluffing.game.scheduler.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class MatchUser {
    private UUID userId;
    private Short userGameNumber;
}
