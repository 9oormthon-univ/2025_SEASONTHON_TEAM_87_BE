package com.developing.bluffing.game.scheduler.dto;

import com.developing.bluffing.game.entity.enums.GameTeam;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VoteResult {

    private Short userNumber;
    private Short result;
    private GameTeam userTeam;

}
