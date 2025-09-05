package com.developing.bluffing.game.controller;

import com.developing.bluffing.game.convertor.GameFactory;
import com.developing.bluffing.game.dto.request.GameChatMessageRequest;
import com.developing.bluffing.game.dto.request.GameMatchRequest;
import com.developing.bluffing.game.dto.response.GameChatMessageResponse;
import com.developing.bluffing.security.entity.UserDetailImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class GameStompController {

    private final SimpMessagingTemplate messaging;

    // 클라이언트 → 서버 (채팅 전송)
    // 클라에서 stomp.send("/api/v1/game/chat/message", {...})
    @MessageMapping("/message")
    public void handleChat(
            @Payload GameChatMessageRequest request,
            @AuthenticationPrincipal UserDetailImpl userDetail) {
        // 서버 → 방 전체 브로드캐스트
        GameChatMessageResponse msg = GameFactory.toGameChatMessageResponse(request);
        messaging.convertAndSend(
                "/api/v1/game/server/room/" + request.getRoomId(),
                msg
        );
    }

    // 클라이언트 → 서버 (매칭 요청)
    // 클라에서 stomp.send("/api/v1/game/chat/match", {...})
    @MessageMapping("/match")
    public void handleMatch(
            @Payload GameMatchRequest request,
            @AuthenticationPrincipal UserDetailImpl userDetail) {

        //match로직 아래포함해서 넣어야함
        //매칭 서비스 구현 예정
        // 서버 → 특정 유저 1:1 메시지
        messaging.convertAndSendToUser(
                userDetail.getUser().getId(),
                "/api/v1/game/match/notify",
                new MatchResponse(request.roomId(), "매칭 성공!")
        );
    }

    public record MatchResponse(String roomId, String status) {
    }
}
