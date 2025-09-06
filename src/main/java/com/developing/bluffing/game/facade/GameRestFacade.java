package com.developing.bluffing.game.facade;

import com.developing.bluffing.game.dto.request.GameReadyRequest;
import com.developing.bluffing.game.dto.request.GameVoteRequest;
import com.developing.bluffing.game.dto.response.UserGameRecord;
import com.developing.bluffing.user.entity.Users;

public interface GameRestFacade {


    //페이즈 확인 -> 챗방 투표 상황 확인 -> 모두 투표 완료 시 집계 스케쥴 등록
    void voteAndScheduleFlag(Users user, GameVoteRequest r);

    //방 조회 -> 유저정보 조회 및 레디 수정 -> 방 준비 상태확인 -> 준비 완료시 게임시작 스케쥴 시작
    void readyAndScheduleFlag(Users user, GameReadyRequest r);

    UserGameRecord getUserRecord(Users user);
}
