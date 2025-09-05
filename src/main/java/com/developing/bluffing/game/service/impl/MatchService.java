package com.developing.bluffing.game.service.impl;

import com.developing.bluffing.game.entity.ChatRoom;
import com.developing.bluffing.game.entity.UserInGameInfo;
import com.developing.bluffing.game.entity.enums.*;
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

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomService chatRoomService;
    private final UserInGameInfoService userInGameInfoService;

    private static final int ROOM_SIZE = 6; // 방 최소 인원

    // 매칭 대기열
    private final Map<MatchCategory, Queue<Users>> queues = new HashMap<>();

    public synchronized void enqueue(Users user, MatchCategory matchCategory) {
        queues.computeIfAbsent(matchCategory, k -> new ConcurrentLinkedQueue<>()).add(user);
        tryMatch(matchCategory);
    }

    private void tryMatch(MatchCategory matchCategory) {
        Queue<Users> queue = queues.get(matchCategory);
        if (queue.size() < ROOM_SIZE) return; // 인원 부족 시 종료

        // ROOM_SIZE 만큼 뽑기
        List<Users> matchedUsers = new ArrayList<>();
        for (int i = 0; i < ROOM_SIZE; i++) {
            Users u = queue.poll();
            if (u != null) matchedUsers.add(u);
        }
        if (matchedUsers.size() < ROOM_SIZE) { // 부족하면 롤백
            matchedUsers.forEach(queue::add);
            return;
        }

        // ChatRoom 생성
        ChatRoom room = chatRoomService.saveOrThrow(
                ChatRoom.builder()
                        .matchCategory(matchCategory)
                        .gamePhase(GamePhase.WAIT)
                        .winnerTeam(null)
                        .maxPlayer((short) matchedUsers.size())
                        .currentPlayer((short) matchedUsers.size())
                        .topic(ChatTopic.values()[new Random().nextInt(ChatTopic.values().length)])
                        .taggerAge(AgeGroup.from(matchedUsers.get(0).getBirth()))
                        .taggerNumber((short)1)
                        .build()
        );

        // UserInGameInfo 생성 및 DB 저장
        short number = 1;
        for (Users u : matchedUsers) {
            GameTeam team = (number == 1) ? GameTeam.MAFIA : GameTeam.CITIZEN; // 1명 마피아, 나머지 시민 예시
            userInGameInfoService.saveOrThrow(
                    UserInGameInfo.builder()
                            .user(u)
                            .chatRoom(room)
                            .userAge(AgeGroup.from(u.getBirth()))
                            .userTeam(team)
                            .userNumber(number)
                            .readyFlag(false)
                            .build()
            );
            number++;
        }

        // 각 유저에게 STOMP 브로드캐스트
        for (Users u : matchedUsers) {
            GameMatchedResponse response = GameMatchedResponse.builder()
                    .userRoomNumber(userInGameInfoService.getByUserAndChatRoom(u, room).getUserNumber())
                    .userAge(userInGameInfoService.getByUserAndChatRoom(u, room).getUserAge())
                    .team(userInGameInfoService.getByUserAndChatRoom(u, room).getUserTeam())
                    .citizenTeamAgeList(
                            userInGameInfoService.getByChatRoom(room).stream()
                                    .filter(info -> info.getUserTeam() == GameTeam.CITIZEN)
                                    .map(UserInGameInfo::getUserAge)
                                    .toList()
                    )
                    .mafiaTeamAge(
                            userInGameInfoService.getByChatRoom(room).stream()
                                    .filter(info -> info.getUserTeam() == GameTeam.MAFIA)
                                    .map(UserInGameInfo::getUserAge)
                                    .findFirst()
                                    .orElse(null)
                    )
                    .build();

            messagingTemplate.convertAndSendToUser(
                    u.getId().toString(),
                    "/api/v1/game/match/notify",
                    response
            );
        }
    }
}


