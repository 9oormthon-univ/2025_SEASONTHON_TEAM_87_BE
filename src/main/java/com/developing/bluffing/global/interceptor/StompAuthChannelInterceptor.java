package com.developing.bluffing.global.interceptor;

import com.developing.bluffing.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String auth = accessor.getFirstNativeHeader("Authorization");
            if (auth == null || !auth.startsWith("Bearer ")) {
                try {
                    throw new AccessDeniedException("Missing Authorization"); // => ERROR 후 종료
                } catch (AccessDeniedException e) {
                    throw new RuntimeException(e);
                }
            }
            String token = auth.substring(7);
            UUID userId = jwtUtil.getSubjectFromAccessToken(token); // 검증 로직 포함
            accessor.setUser(new UsernamePasswordAuthenticationToken(userId, null, List.of()));
        }

        return message;
    }

}
