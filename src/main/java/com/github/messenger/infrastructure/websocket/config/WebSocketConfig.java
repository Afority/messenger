package com.github.messenger.infrastructure.websocket.config;

import com.github.messenger.infrastructure.websocket.infrastructure.WebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final WebSocketHandler customWebSocketHandler;

    public WebSocketConfig(WebSocketHandler customWebSocketHandler) {
        this.customWebSocketHandler = customWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(customWebSocketHandler, "/ws")
                .setAllowedOrigins("*");
    }

}
