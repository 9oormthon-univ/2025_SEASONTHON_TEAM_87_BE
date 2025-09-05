package com.developing.bluffing.game.scheduler;

import com.developing.bluffing.game.convertor.GameFactory;
import com.developing.bluffing.game.dto.response.GamePhaseChangeResponse;
import com.developing.bluffing.game.dto.response.GameVoteResultResponse;
import com.developing.bluffing.game.entity.ChatRoom;
import com.developing.bluffing.game.entity.UserInGameInfo;
import com.developing.bluffing.game.entity.enums.GamePhase;
import com.developing.bluffing.game.scheduler.dto.VoteResult;
import com.developing.bluffing.game.scheduler.task.GameRoomTask;
import com.developing.bluffing.game.service.ChatRoomService;
import com.developing.bluffing.game.service.UserInGameInfoService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PhaseScheduler implements Runnable {

    private final SimpMessagingTemplate messaging;
    private final ChatRoomService chatRoomService;
    private final UserInGameInfoService userInGameInfoService;

    private final DelayQueue<GameRoomTask> queue = new DelayQueue<>();
    private final ExecutorService worker = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "phase-scheduler");
        t.setDaemon(true);
        return t;
    });

    // 최신 예약을 추적해 오래된 예약 제거(취소)할 때 사용
    private final Map<UUID, GameRoomTask> latestTaskByRoom = new ConcurrentHashMap<>();

    private volatile boolean running = true;

    @PostConstruct
    public void start() {
        worker.submit(this);
    }

    @PreDestroy
    public void stop() {
        running = false;
        worker.shutdownNow();
    }

    /**
     * roomId에 phase를 delayMs 뒤에 실행하도록 예약 (기존 예약 있으면 교체)
     */
    public void schedule(GameRoomTask task) {
        // 최신 예약 교체(있으면 큐에서 제거)
        Optional.ofNullable(latestTaskByRoom.put(task.getRoomId(), task))
                .ifPresent(queue::remove);

        queue.offer(task);
    }


    /**
     * 특정 방의 예약 취소
     */
    public void cancel(UUID roomId) {
        Optional.ofNullable(latestTaskByRoom.remove(roomId))
                .ifPresent(t -> {
                    queue.remove(t);
                    log.info("[CANCEL] {}", t);
                });
    }

    @Override
    public void run() {
        while (running) {
            try {
                GameRoomTask task = queue.take(); // 시간이 되면 반환(블로킹)
                // 최신 예약인지 확인 (오래된 예약이면 무시)
                if (latestTaskByRoom.get(task.getRoomId()) != task) {
                    log.debug("[SKIP-STALE] {}", task);
                    continue;
                }
                process(task);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            } catch (Throwable t) {
                log.error("PhaseScheduler loop error", t);
            }
        }
    }

    //추가 로직 설계해야함
    private void process(GameRoomTask task) {
        GamePhase nowGamePhase = task.getPhase();
        switch (nowGamePhase) {
            case CHAT -> {
                //채팅이 끝난 후 로직
                ChatRoom chatRoom =
                        chatRoomService.updatePhaseById(task.getRoomId(), GamePhase.VOTE);

                GamePhaseChangeResponse msg
                        = GameFactory.toGamePhaseChangeResponse(task, "Chat Finish And Vote Start");
                messaging.convertAndSend(
                        "/api/v1/game/server/room/" + chatRoom.getId(),
                        msg
                );
                // 채팅 종료 브로드 캐스팅 후 투표 전환
                schedule(GameFactory.toGameRoomTask(task, GamePhase.VOTE));
            }
            case VOTE,RE_VOTE -> {
                // 투표시간 기다린 후 로직
                ChatRoom chatRoom =
                        chatRoomService.updatePhaseById(task.getRoomId(), GamePhase.VOTE_RESULT);
                //투표가 끝남을 알림
                GamePhaseChangeResponse msg
                        = GameFactory.toGamePhaseChangeResponse(task, "Vote Finish");
                messaging.convertAndSend(
                        "/api/v1/game/server/room/" + chatRoom.getId(),
                        msg
                );
                // 투표 종료 브로드 캐스팅 후 투표 집계로 전환
                schedule(GameFactory.toGameRoomTask(task, GamePhase.VOTE_RESULT));
            }
            case VOTE_RESULT -> {
                //게임 종료로 변경
                ChatRoom chatRoom =
                        chatRoomService.getById(task.getRoomId());

                //TODO : 리팩토링시 위에 채팅 조회 안하고 id조회로 바꾸기
                List<UserInGameInfo> userInGameInfos =
                        userInGameInfoService.getByChatRoom(chatRoom);
                List<VoteResult> voteResults = userInGameInfoService
                        .voteResult(userInGameInfos);

                //최다 득표자 찾기 여기서 분기
                List<VoteResult> winners = findWinners(voteResults);
                if (winners.size() == 1) {
                    VoteResult winner = winners.getFirst();
                    ChatRoom gameResult =
                            chatRoomService.updateChatResult(chatRoom.getId(), winner.getUserTeam());

                    GameVoteResultResponse msg
                            = GameFactory.toGameVoteResultResponse(gameResult, voteResults);
                    messaging.convertAndSend(
                            "/api/v1/game/server/room/" + gameResult.getId(),
                            msg
                    );

                    GameRoomTask removed = latestTaskByRoom.remove(gameResult.getId());
                    if (removed != null) { queue.remove(removed); }
                } else {
                    //우승자가 1명이 아닌 경우 다시 시작
                    ChatRoom reChatRoom =
                            chatRoomService.updatePhaseById(task.getRoomId(), GamePhase.RE_VOTE);
                    GameRoomTask newTask = GameFactory.toGameRoomTask(task, reChatRoom.getGamePhase());
                    schedule(newTask);

                    GamePhaseChangeResponse msg
                            = GameFactory.toGamePhaseChangeResponse(newTask, "ReVote start");
                    messaging.convertAndSend(
                            "/api/v1/game/server/room/" + chatRoom.getId(),
                            msg
                    );
                }
            }
        }
    }

    private List<VoteResult> findWinners(List<VoteResult> results) {
        if (results.isEmpty()) return List.of();

        // 최댓값 찾기
        short maxVotes = results.stream()
                .map(VoteResult::getResult)
                .max(Short::compare)
                .orElse((short) 0);

        // 최댓값과 같은 후보들 모두 반환
        return results.stream()
                .filter(r -> r.getResult() == maxVotes)
                .toList();
    }
}