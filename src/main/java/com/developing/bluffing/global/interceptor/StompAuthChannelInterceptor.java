package com.developing.bluffing.global.interceptor;

import com.developing.bluffing.security.util.JwtUtil;
import com.developing.bluffing.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final UserService userService; // 필요시 DB 조회

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) return message;

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String auth = accessor.getFirstNativeHeader("Authorization");
            if (auth == null || !auth.startsWith("Bearer ")) {
                log.warn("[STOMP][CONNECT] Authorization 헤더 누락: {}", auth);
                throw new org.springframework.messaging.MessagingException("Missing Authorization");
            }

            String token = auth.substring(7).trim();
            UUID userId = jwtUtil.getSubjectFromAccessToken(token);

            // principal 로 UUID 문자열 심기
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId.toString(), null, List.of());
            accessor.setUser(authentication);

            // 로그 찍기
            log.info("[STOMP][CONNECT] userId={} sessionId={}", userId, accessor.getSessionId());
        }

        return message;
    }
}
