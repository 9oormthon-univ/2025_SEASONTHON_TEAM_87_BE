package com.developing.bluffing.game.controller;

import com.developing.bluffing.game.convertor.GameFactory;
import com.developing.bluffing.game.dto.request.GameChatMessageRequest;
import com.developing.bluffing.game.dto.request.GameMatchRequest;
import com.developing.bluffing.game.dto.response.GameChatMessageResponse;
import com.developing.bluffing.security.entity.UserDetailImpl;
import com.developing.bluffing.game.service.MatchService;
import com.developing.bluffing.user.entity.Users;
import com.developing.bluffing.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import java.security.Principal;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GameStompController {

    private final SimpMessagingTemplate messaging;
    private final MatchService matchService; // ✅ 인터페이스 주입
    private final UserService userService;

    @MessageMapping("/message")
    public void handleChat(
            @Payload GameChatMessageRequest request,
            @AuthenticationPrincipal UserDetailImpl userDetail) {
        GameChatMessageResponse msg = GameFactory.toGameChatMessageResponse(request);
        messaging.convertAndSend(
                "/api/v1/game/server/room/" + request.getRoomId(),
                msg
        );
    }

    @MessageMapping("/match")
    public void handleMatch(
            @Payload GameMatchRequest request,
            Principal Principal) {

        UUID userId = UUID.fromString(Principal.getName());
        Users user = userService.getById(userId);
        log.info("[MESSAGE] [MATCH] userInfo id : {} username : {} userLoginId : {}",user.getId(),user.getName(),user.getLoginId());
        matchService.enqueue( user , request.getMatchCategory());
    }

    public record MatchResponse(String roomId, String status) {}
}
