package com.developing.bluffing.game.dto.response;

import com.developing.bluffing.game.entity.enums.AgeGroup;
import com.developing.bluffing.game.entity.enums.GameTeam;
import com.developing.bluffing.game.scheduler.dto.VoteResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class GameVoteResultResponse {
    private GameTeam winnerTeam;
    private List<VoteResult> voteResult;
    private Short taggerNumber;
    private AgeGroup taggerAge;
    private UUID roomId;
}
