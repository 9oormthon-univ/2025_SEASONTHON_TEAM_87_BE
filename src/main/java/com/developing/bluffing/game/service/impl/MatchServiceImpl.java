package com.developing.bluffing.game.service.impl;

import com.developing.bluffing.game.entity.ChatRoom;
import com.developing.bluffing.game.entity.UserInGameInfo;
import com.developing.bluffing.game.entity.Users;
import com.developing.bluffing.game.entity.enums.*;
import com.developing.bluffing.game.dto.response.GameMatchedResponse;
import com.developing.bluffing.game.service.ChatRoomService;
import com.developing.bluffing.game.service.MatchService;
import com.developing.bluffing.game.service.UserInGameInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomService chatRoomService;
    private final UserInGameInfoService userInGameInfoService;

    private static final int ROOM_SIZE = 6;

    private final Map<MatchCategory, Queue<Users>> queues = new HashMap<>();

    private int calculateAge(LocalDate birth) {
        return Period.between(birth, LocalDate.now()).getYears();
    }

    @Override
    public synchronized void enqueue(Users user, MatchCategory matchCategory) {
        queues.computeIfAbsent(matchCategory, k -> new ConcurrentLinkedQueue<>()).add(user);
        tryMatch(matchCategory);
    }

    private void tryMatch(MatchCategory matchCategory) {
        Queue<Users> queue = queues.get(matchCategory);
        if (queue.size() < ROOM_SIZE) return;

        List<Users> matchedUsers = new ArrayList<>();
        for (int i = 0; i < ROOM_SIZE; i++) {
            Users u = queue.poll();
            if (u != null) matchedUsers.add(u);
        }

        if (matchedUsers.size() < ROOM_SIZE) {
            matchedUsers.forEach(queue::add);
            return;
        }

        ChatRoom room = chatRoomService.saveOrThrow(
                ChatRoom.builder()
                        .matchCategory(matchCategory)
                        .gamePhase(GamePhase.WAIT)
                        .winnerTeam(null)
                        .maxPlayer((short) matchedUsers.size())
                        .currentPlayer((short) matchedUsers.size())
                        .topic(ChatTopic.values()[new Random().nextInt(ChatTopic.values().length)])
                        .taggerAge(AgeGroup.fromAge(calculateAge(matchedUsers.get(0).getBirth())))
                        .taggerNumber((short)1)
                        .build()
        );

        short number = 1;
        for (Users u : matchedUsers) {
            GameTeam team = (number == 1) ? GameTeam.MAFIA : GameTeam.CITIZEN;
            userInGameInfoService.saveOrThrow(
                    UserInGameInfo.builder()
                            .user(u)
                            .chatRoom(room)
                            .userAge(AgeGroup.fromAge(calculateAge(u.getBirth())))
                            .userTeam(team)
                            .userNumber(number)
                            .readyFlag(false)
                            .build()
            );
            number++;
        }

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




