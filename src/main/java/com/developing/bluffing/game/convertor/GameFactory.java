package com.developing.bluffing.game.convertor;

import com.developing.bluffing.game.dto.enums.MessageReference;
import com.developing.bluffing.game.dto.request.GameChatMessageRequest;
import com.developing.bluffing.game.dto.response.GameChatMessageResponse;
import com.developing.bluffing.game.dto.response.GamePhaseChangeResponse;
import com.developing.bluffing.game.dto.response.GameReVoteResponse;
import com.developing.bluffing.game.dto.response.GameVoteResultResponse;
import com.developing.bluffing.game.entity.ChatRoom;
import com.developing.bluffing.game.entity.UserInGameInfo;
import com.developing.bluffing.game.entity.enums.GamePhase;
import com.developing.bluffing.game.scheduler.dto.MatchUser;
import com.developing.bluffing.game.scheduler.dto.VoteResult;
import com.developing.bluffing.game.scheduler.task.GameRoomTask;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.IntStream;

public class GameFactory {
    public static GameChatMessageResponse toGameChatMessageResponse(GameChatMessageRequest r){
        return GameChatMessageResponse.builder()
                .content(r.getContent())
                .roomId(r.getRoomId())
                .senderNumber(r.getSenderNumber())
                .sendTime(r.getSendTime())
                .messageReference(MessageReference.USER)
                .build();
    }

    public static GameRoomTask toGameRoomTask(ChatRoom chatRoom, GamePhase phase, List<UserInGameInfo> gameUsers) {
        List<MatchUser> matchUsers =
                IntStream.range(0, gameUsers.size()) // 0 ~ n-1
                        .mapToObj(i -> MatchUser.builder()
                                .userId(gameUsers.get(i).getUser().getId())
                                .userGameNumber((short) (i + 1)) // 1번부터 시작
                                .build())
                        .toList();
        return GameRoomTask.builder()
                .roomId(chatRoom.getId())
                .phase(phase)
                .executeAtEpochMs(System.currentTimeMillis() + phase.getDefaultDurationMs())
                .matchUsers(matchUsers)
                .build();
    }


    //페이즈 변경 후 다시
    public static GameRoomTask toGameRoomTask(GameRoomTask task,GamePhase phase) {
        return GameRoomTask.builder()
                .roomId(task.getRoomId())
                .phase(phase)
                .executeAtEpochMs(System.currentTimeMillis() + phase.getDefaultDurationMs())
                .matchUsers(task.getMatchUsers())
                .build();
    }

    public static MatchUser toMatchUser(UserInGameInfo gameUsers,Short userRoomNum){
        return MatchUser.builder()
                .userId(gameUsers.getUser().getId())
                .userGameNumber(userRoomNum)
                .build();
    }

    public static GameVoteResultResponse toGameVoteResultResponse(ChatRoom chatRoom, List<VoteResult> voteResults){
        return GameVoteResultResponse.builder()
                .voteResult(voteResults)
                .taggerAge(chatRoom.getTaggerAge())
                .winnerTeam(chatRoom.getWinnerTeam())
                .taggerNumber(chatRoom.getTaggerNumber())
                .roomId(chatRoom.getId())
                .build();
    }

    public static GameReVoteResponse toGameReVoteResponse(List<Short> winnersNumbers ,List<VoteResult> voteResults){
        return GameReVoteResponse.builder()
                .sameRateUserNumber(winnersNumbers)
                .voteResults(voteResults)
                .phase(GamePhase.RE_VOTE)
                .build();
    }

    public static GamePhaseChangeResponse toGamePhaseChangeResponse(GameRoomTask task,String msg){
        ZoneId seoul = ZoneId.of("Asia/Seoul");
        return GamePhaseChangeResponse.builder()
                .changeTime(LocalDateTime.now(seoul))
                .phase(task.getPhase())
                .roomId(task.getRoomId())
                .content(msg)
                .messageReference(MessageReference.SERVER)
                .build();
    }
}
