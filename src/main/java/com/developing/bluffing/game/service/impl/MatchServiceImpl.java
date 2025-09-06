package com.developing.bluffing.game.service.impl;

import com.developing.bluffing.game.entity.ChatRoom;
import com.developing.bluffing.game.entity.UserInGameInfo;
import com.developing.bluffing.user.entity.Users;
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

    // =============================
    // 테스트용: 2명, 첫 번째 유저가 마피아
    // =============================
    private static final int ROOM_SIZE = 2;

    // =============================
    // 원래 코드 (6명 기준, 연령대 다른 1명이 마피아)
    // =============================
    // private static final int ROOM_SIZE = 6;

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

        // =============================
        // 원래 코드 주석 처리 시작
        // =============================
        /*
        // --- 연령대 분류 ---
        Map<AgeGroup, List<Users>> ageGroups = new HashMap<>();
        for (Users u : matchedUsers) {
            AgeGroup age = AgeGroup.fromAge(calculateAge(u.getBirth()));
            ageGroups.computeIfAbsent(age, k -> new ArrayList<>()).add(u);
        }

        List<Users> sameAgeGroup = new ArrayList<>();
        List<Users> differentAgeGroup = new ArrayList<>();

        // 가장 많은 연령대 그룹부터 5명 채우고 나머지는 다른 연령대로
        ageGroups.entrySet().stream()
                .sorted((a, b) -> b.getValue().size() - a.getValue().size())
                .forEach(entry -> {
                    for (Users u : entry.getValue()) {
                        if (sameAgeGroup.size() < 5) sameAgeGroup.add(u);
                        else differentAgeGroup.add(u);
                    }
                });

        List<Users> finalMatch = new ArrayList<>();
        finalMatch.addAll(sameAgeGroup);
        if (!differentAgeGroup.isEmpty()) finalMatch.add(differentAgeGroup.get(0));
        */
        // =============================
        // 원래 코드 주석 처리 끝
        // =============================

        // =============================
        // 테스트용 코드: 2명, 첫 번째 유저 마피아
        // =============================
        List<Users> sameAgeGroup = new ArrayList<>();
        List<Users> differentAgeGroup = new ArrayList<>();
        // 첫 번째 유저 마피아
        differentAgeGroup.add(matchedUsers.get(0));
        // 두 번째 유저 시민
        sameAgeGroup.add(matchedUsers.get(1));

        List<Users> finalMatch = new ArrayList<>();
        finalMatch.addAll(sameAgeGroup);
        finalMatch.addAll(differentAgeGroup);

        // --- 방 생성 ---
        ChatRoom room = chatRoomService.saveOrThrow(
                ChatRoom.builder()
                        .matchCategory(matchCategory)
                        .gamePhase(GamePhase.WAIT)
                        .winnerTeam(null)
                        .maxPlayer((short) finalMatch.size())
                        .currentPlayer((short) finalMatch.size())
                        .topic(ChatTopic.values()[new Random().nextInt(ChatTopic.values().length)])
                        .taggerAge(calculateAge(finalMatch.get(0).getBirth()) >= 0
                                ? AgeGroup.fromAge(calculateAge(finalMatch.get(0).getBirth()))
                                : AgeGroup.HUNDREDS)
                        .taggerNumber((short)1)
                        .build()
        );

        // --- 팀 배정 ---
        for (int i = 0; i < finalMatch.size(); i++) {
            Users u = finalMatch.get(i);
            GameTeam team;

            // 테스트용: 첫 번째 유저 마피아
            if (differentAgeGroup.contains(u)) {
                team = GameTeam.MAFIA;
            } else {
                team = GameTeam.CITIZEN;
            }

            userInGameInfoService.saveOrThrow(
                    UserInGameInfo.builder()
                            .user(u)
                            .chatRoom(room)
                            .userAge(AgeGroup.fromAge(calculateAge(u.getBirth())))
                            .userTeam(team)
                            .userNumber((short)(i + 1))
                            .readyFlag(false)
                            .build()
            );
        }

        // --- 클라이언트에 알림 ---
        for (Users u : finalMatch) {
            UserInGameInfo info = userInGameInfoService.getByUserAndChatRoom(u, room);
            GameMatchedResponse response = GameMatchedResponse.builder()
                    .userRoomNumber(info.getUserNumber())
                    .userAge(info.getUserAge())
                    .team(info.getUserTeam())
                    .citizenTeamAgeList(
                            userInGameInfoService.getByChatRoom(room).stream()
                                    .filter(i -> i.getUserTeam() == GameTeam.CITIZEN)
                                    .map(UserInGameInfo::getUserAge)
                                    .toList()
                    )
                    .mafiaTeamAge(
                            userInGameInfoService.getByChatRoom(room).stream()
                                    .filter(i -> i.getUserTeam() == GameTeam.MAFIA)
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








