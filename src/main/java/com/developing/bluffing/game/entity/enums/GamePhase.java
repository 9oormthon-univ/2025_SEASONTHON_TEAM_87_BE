package com.developing.bluffing.game.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GamePhase {

    WAIT(21_000),
    CHAT(301_000),         // 5분 토론 네트워크 및 랜더링 시간 생각해서 모두 +1초씩 줬음
    VOTE(61_000),          // 1분 투표
    RE_VOTE(61_000),
    VOTE_RESULT(1_000),
    END(2_000),
    RE_VOTE_RESULT(2_000);    // 1초 결과

    private final long defaultDurationMs;

}