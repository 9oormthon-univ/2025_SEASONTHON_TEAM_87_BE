package com.developing.bluffing.game.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GamePhase {
    CHAT(301_000),         // 5분 토론 네트워크 및 랜더링 시간 생각해서 모두 +1초씩 줬음
    VOTE(61_000),          // 1분 투표
    RESULT(2_000);    // 1초 결과

    private final long defaultDurationMs;

}