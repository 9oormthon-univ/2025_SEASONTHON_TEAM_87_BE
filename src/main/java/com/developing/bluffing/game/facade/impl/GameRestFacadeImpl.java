package com.developing.bluffing.game.facade.impl;

import com.developing.bluffing.game.convertor.GameFactory;
import com.developing.bluffing.game.dto.request.GameReadyRequest;
import com.developing.bluffing.game.dto.request.GameVoteRequest;
import com.developing.bluffing.game.dto.response.GamePhaseChangeResponse;
import com.developing.bluffing.game.entity.ChatRoom;
import com.developing.bluffing.game.entity.UserInGameInfo;
import com.developing.bluffing.game.entity.enums.GamePhase;
import com.developing.bluffing.game.exception.GameException;
import com.developing.bluffing.game.exception.errorCode.GameErrorCode;
import com.developing.bluffing.game.facade.GameRestFacade;
import com.developing.bluffing.game.scheduler.PhaseScheduler;
import com.developing.bluffing.game.scheduler.task.GameRoomTask;
import com.developing.bluffing.game.service.ChatRoomService;
import com.developing.bluffing.game.service.UserInGameInfoService;
import com.developing.bluffing.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameRestFacadeImpl implements GameRestFacade {

    private final PhaseScheduler scheduler;
    private final ChatRoomService chatRoomService;
    private final UserInGameInfoService userInGameInfoService;


    private final SimpMessagingTemplate messaging;

    //페이즈 확인 -> 챗방 투표 상황 확인 -> 모두 투표 완료 시 집계 스케쥴 등록
    @Transactional(readOnly = false)
    @Override
    public void voteAndScheduleFlag(Users user, GameVoteRequest r){
        ChatRoom chatRoom = chatRoomService.getById(r.getChatRoomId());
        //페이즈 확인
        if (chatRoom.getGamePhase() != GamePhase.VOTE){
            throw new GameException(GameErrorCode.GAME_NOT_NOW_PHASE);
        }

        //투표 저장
        UserInGameInfo gameUserInfo = userInGameInfoService.getByUserAndChatRoom(user, chatRoom);
        if (gameUserInfo.getVotedUserNumber().equals(gameUserInfo.getUserNumber())){
            throw new GameException(GameErrorCode.GAME_CANT_VOTE_SELF);
        }
        gameUserInfo.vote(r.getVotedUserNumber());
        UserInGameInfo savedInfo = userInGameInfoService.saveOrThrow(gameUserInfo);

        //투표 상황 확인
        long voteCount = userInGameInfoService.countVote(chatRoom);
        short chatCurrentPlayer = chatRoom.getCurrentPlayer();
        if(voteCount >= chatCurrentPlayer){
            //여기에 스케쥴 등록 로직
        }
    }

    //방 조회 -> 유저정보 조회 및 레디 수정 -> 방 준비 상태확인 -> 준비 완료시 게임시작 스케쥴 시작
    @Transactional(readOnly = false)
    @Override
    public void readyAndScheduleFlag(Users user, GameReadyRequest r){
        ChatRoom chatRoom = chatRoomService.getById(r.getChatRoomId());
        UserInGameInfo userInGameInfo = userInGameInfoService.getByUserAndChatRoom(user,chatRoom);
        userInGameInfo.ready();
        userInGameInfoService.saveOrThrow(userInGameInfo);

        long readyCount = userInGameInfoService.countReady(chatRoom);
        short chatCurrentPlayer = chatRoom.getCurrentPlayer();

        //다 되었을 경우 채팅 시작
        if(readyCount >= chatCurrentPlayer){
            //채팅방 스케쥴 TASK 생성
            List<UserInGameInfo> gameUsers = userInGameInfoService.getByChatRoom(chatRoom);
            GameRoomTask task = GameFactory.toGameRoomTask(chatRoom,GamePhase.CHAT,gameUsers);

            GamePhaseChangeResponse response
                    = GameFactory.toGamePhaseChangeResponse(task,"chat start");
            // 매칭시 클라이언트에서 방id를 구독해야 확인 가능
            messaging.convertAndSend(
                    "/api/v1/game/server/room/" + task.getRoomId(),
                    response
            );
            scheduler.schedule(task);
        }
    }

}
