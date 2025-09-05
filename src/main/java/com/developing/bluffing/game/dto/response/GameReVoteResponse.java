package com.developing.bluffing.game.dto.response;

import com.developing.bluffing.game.entity.enums.GamePhase;
import com.developing.bluffing.game.scheduler.dto.MatchUser;
import com.developing.bluffing.game.scheduler.dto.VoteResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class GameReVoteResponse {

    private List<Short> sameRateUserNumber;
    private List<VoteResult> voteResults;
    private GamePhase phase;


}
