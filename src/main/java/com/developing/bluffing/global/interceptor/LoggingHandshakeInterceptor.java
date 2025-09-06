package com.developing.bluffing.global.interceptor;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class LoggingHandshakeInterceptor implements HandshakeInterceptor {
    @Override public boolean beforeHandshake(ServerHttpRequest req, ServerHttpResponse res,
                                             WebSocketHandler h, Map<String,Object> attrs) {
        System.out.println("Req Sec-WebSocket-Protocol: " + req.getHeaders().get("Sec-WebSocket-Protocol"));
        return true;
    }
    @Override public void afterHandshake(ServerHttpRequest req, ServerHttpResponse res,
                                         WebSocketHandler h, Exception ex) {
        System.out.println("Res Sec-WebSocket-Protocol: " + res.getHeaders().get("Sec-WebSocket-Protocol"));
    }
}

