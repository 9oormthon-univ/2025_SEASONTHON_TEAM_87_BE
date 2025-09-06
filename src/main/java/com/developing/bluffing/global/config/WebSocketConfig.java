package com.developing.bluffing.global.config;

import com.developing.bluffing.global.interceptor.LoggingHandshakeInterceptor;
import com.developing.bluffing.global.interceptor.StompAuthChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompAuthChannelInterceptor stompAuthChannelInterceptor;
    private final LoggingHandshakeInterceptor loggingHandshakeInterceptor;

    @Bean
    public DefaultHandshakeHandler stompHandshakeHandler() {
        DefaultHandshakeHandler h = new DefaultHandshakeHandler();
        // ★ STOMP 서브프로토콜을 명시적으로 지원
        h.setSupportedProtocols("v12.stomp", "v11.stomp", "v10.stomp");
        return h;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry r) {
        // 1) 순수 WebSocket
        r.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .setHandshakeHandler(stompHandshakeHandler())
                .addInterceptors(loggingHandshakeInterceptor);

        r.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .addInterceptors(loggingHandshakeInterceptor)
                .withSockJS()
                .setSessionCookieNeeded(false);
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

    @Override
    public void configureClientInboundChannel(ChannelRegistration reg) {
        reg.interceptors(stompAuthChannelInterceptor);
    }

}
