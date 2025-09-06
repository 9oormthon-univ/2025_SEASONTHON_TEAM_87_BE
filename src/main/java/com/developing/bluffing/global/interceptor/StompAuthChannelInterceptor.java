package com.developing.bluffing.global.interceptor;

import com.developing.bluffing.security.entity.UserDetailImpl;
import com.developing.bluffing.security.util.JwtUtil;
import com.developing.bluffing.user.entity.Users;
import com.developing.bluffing.user.service.UserService;
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
    private final UserService userService; // 또는 UsersService

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) return message;

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String auth = accessor.getFirstNativeHeader("Authorization");
            if (auth == null || !auth.startsWith("Bearer ")) {
                throw new org.springframework.messaging.MessagingException("Missing Authorization");
            }

            String token = auth.substring(7);
            UUID userId = jwtUtil.getSubjectFromAccessToken(token);

            Users user = userService.getById(userId);
            UserDetailImpl userDetail = new UserDetailImpl(user); // ← 네 구현에 맞게 생성
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetail, null, userDetail.getAuthorities());

            accessor.setUser(authentication); // ★ 이제 @AuthenticationPrincipal UserDetailImpl 주입 OK
        }

        return message;
    }
}


