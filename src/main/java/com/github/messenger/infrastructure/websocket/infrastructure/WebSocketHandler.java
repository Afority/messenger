package com.github.messenger.infrastructure.websocket.infrastructure;

import com.github.messenger.domain.events_system.EventBroker;
import com.github.messenger.domain.events_system.event_messages.UserLoggedOutEventMessage;
import com.github.messenger.domain.events_system.event_messages.UserLoggingInEventMessage;
import com.github.messenger.domain.value_objects.UserId;
import com.github.messenger.infrastructure.helper.BearerTokenExtractorHelper;
import com.github.messenger.infrastructure.security.JwtCore;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class WebSocketHandler extends AbstractWebSocketHandler {
    private final JwtCore jwtCore;
    private final UsersSessions usersSessions;
    private final RequestHandler requestHandler;
    private final ExecutorService executorService;
    private final EventBroker eventBroker;

    public WebSocketHandler(
            RequestHandler requestHandler,
            JwtCore jwtCore,
            BearerTokenExtractorHelper bearerTokenExtractorHelper,
            UsersSessions usersSessions,
            EventBroker eventBroker) {
        this.jwtCore = jwtCore;
        this.requestHandler = requestHandler;
        this.usersSessions = usersSessions;
        this.eventBroker = eventBroker;

        executorService = Executors.newVirtualThreadPerTaskExecutor();
    }

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) throws Exception {
        String jwtToken = extractToken(session);

        if (jwtToken == null) {
            // todo переделать
            session.sendMessage(new TextMessage("Authorization header required"));
            session.close();
        }
        else if (jwtCore.verifyToken(jwtToken)) {
            UserId userId = new UserId(jwtCore.getUserIdFromToken(jwtToken));

            boolean hasOtherSessions = usersSessions.getSession(userId) != null;

            ClientConnection clientConnection = usersSessions.addWebSocketSession(userId, session);
            clientConnection.runSendMessageLoop();

            // если до этого не было сессий пользователя на других устройствах - регистрируем событие
            if (!hasOtherSessions) {
                System.out.println("Пользователь зашел в ws");
                eventBroker.publishEvent(new UserLoggingInEventMessage(userId));
            }
        } else {
            session.sendMessage(new TextMessage("Invalid authentication"));
            session.close();
        }
    }

    private String extractToken(WebSocketSession session) {
        try {
            Objects.requireNonNull(session.getUri());
            UriComponents uriComponents = UriComponentsBuilder.fromUri(session.getUri()).build();

            return uriComponents.getQueryParams().getFirst("token");
        }
        catch (NullPointerException ignored) {
            return null;
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, @NotNull TextMessage message) throws IOException {
        UserId userId = usersSessions.getUserId(session.getId());

        executorService.submit(() -> {
            requestHandler.handle(
                    message.getPayload(),
                    userId,
                    usersSessions.getSession(userId),
                    session.getId()
            );
        });
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) throws Exception {
        if (usersSessions.removeWebSocketSession(session.getId())) {
            eventBroker.publishEvent(new UserLoggedOutEventMessage(usersSessions.getUserId(session.getId())));
        }
    }
}

