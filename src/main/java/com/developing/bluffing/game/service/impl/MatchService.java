package com.developing.bluffing.game.service.impl;

import com.developing.bluffing.game.entity.ChatRoom;
import com.developing.bluffing.game.entity.UserInGameInfo;
import com.developing.bluffing.game.entity.enums.AgeGroup;
import com.developing.bluffing.game.entity.enums.GameTeam;
import com.developing.bluffing.game.entity.enums.MatchCategory;
import com.developing.bluffing.game.dto.response.GameMatchedResponse;
import com.developing.bluffing.game.service.ChatRoomService;
import com.developing.bluffing.game.service.UserInGameInfoService;
import com.developing.bluffing.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final SimpMessagingTemplate messaging;
    private final ChatRoomService chatRoomService;
    private final UserInGameInfoService userInGameInfoService;

    private static final int ROOM_SIZE = 6; // 최소 매칭 인원

    // 대기열: matchCategory 별 Queue
    private final Map<MatchCategory, Queue<Users>> queueMap = new HashMap<>();

    public synchronized void enqueue(Users user, MatchCategory matchCategory) {
        Queue<Users> queue = queueMap.computeIfAbsent(matchCategory, k -> new ConcurrentLinkedQueue<>());
        queue.add(user);

        if (queue.size() < ROOM_SIZE) return; // 최소 인원 미달 → 대기

        // 매칭 시도
        List<Users> matchedUsers = new ArrayList<>();
        for (int i = 0; i < ROOM_SIZE; i++) {
            matchedUsers.add(queue.poll());
        }

        // ChatRoom 생성
        ChatRoom room = ChatRoom.builder()
                .id(UUID.randomUUID())
                .winnerTeam(null)
                .gamePhase(com.developing.bluffing.game.entity.enums.GamePhase.WAIT)
                .matchCategory(matchCategory)
                .maxPlayer(ROOM_SIZE)
                .currentPlayer(ROOM_SIZE)
                .build();
        chatRoomService.saveOrThrow(room);

        // UserInGameInfo 저장
        short number = 1;
        for (Users u : matchedUsers) {
            GameTeam team = (number == 1) ? GameTeam.MAFIA : GameTeam.CITIZEN; // 예시
            userInGameInfoService.saveOrThrow(
                    UserInGameInfo.builder()
                            .user(u)
                            .chatRoom(room)
                            .gameTeam(team)
                            .userNumber(number++)
                            .readyFlag(false)
                            .build()
            );
        }

        // GameMatchedResponse 생성 및 STOMP 전송
        for (Users u : matchedUsers) {
            GameMatchedResponse response = GameMatchedResponse.builder()
                    .userRoomNumber((short)1) // 예시
                    .userAge(AgeGroup.Y20) // 예시
                    .team(GameTeam.CITIZEN) // 예시
                    .citizenTeamAgeList(List.of(AgeGroup.Y20, AgeGroup.Y30)) // 예시
                    .mafiaTeamAge(AgeGroup.Y20) // 예시
                    .build();

            messaging.convertAndSendToUser(
                    u.getId().toString(),
                    "/api/v1/game/match/notify",
                    response
            );
        }
    }
}
