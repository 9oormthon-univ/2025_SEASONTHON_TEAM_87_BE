package com.developing.bluffing.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry r) {
        r.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry r) {
        // 서버 -> 클라 브로드캐스트 용 (구독 경로)
        r.enableSimpleBroker("/api/v1/game/server","/user");

        // 클라 -> 서버 요청 prefix (@MessageMapping 매핑됨)
        r.setApplicationDestinationPrefixes("/api/v1/game/chat");

        // 서버 -> 특정 유저 1:1 전송 prefix
        r.setUserDestinationPrefix("/api/v1/game/match");
    }

}
