package com.github.messenger.infrastructure.websocket.config;

import com.github.messenger.infrastructure.helper.BearerTokenExtractorHelper;
import com.github.messenger.infrastructure.security.JwtCore;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor {
    JwtCore jwtCore;
    BearerTokenExtractorHelper jwtExtractorHelper;

    @Autowired
    public AuthHandshakeInterceptor(JwtCore jwtCore) {
        this.jwtCore = jwtCore;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {

        MultiValueMap<String, String> params =
                UriComponentsBuilder.fromUri(request.getURI()).build().getQueryParams();

        String token = params.getFirst("token");

        if (token != null) {
            if (jwtCore.verifyToken(token)) {
                attributes.put("userId", String.valueOf(jwtCore.getUserIdFromToken(token)));
                return true;
            }
        }

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, @Nullable Exception exception){

    }
}

