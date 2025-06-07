package com.nicolas.chatapp.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.security.Principal;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final TokenProvider tokenProvider;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        if(request instanceof ServletServerHttpRequest serverHttpRequest) {
            HttpServletRequest httpRequest = serverHttpRequest.getServletRequest();
            String token = httpRequest.getParameter("token");

            if (token != null && token.startsWith("Bearer ")) {
                try {
                    Claims claims = tokenProvider.getClaimsFromToken(token);
                    String email = claims.get("email", String.class);
                    System.out.println("✅ This is email: " + email);

                    // Gán vào session attribute (được dùng ở StompHeaderAccessor.getUser())
                    Principal principal = () -> email;
                    attributes.put("user", principal);
                } catch (Exception e) {
                    System.out.println("❌ Token parsing failed: " + e.getMessage());
                    return false;
                }
            } else {
                System.out.println("❌ Token missing or invalid format");
                return false;
            }
        }


        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }
}