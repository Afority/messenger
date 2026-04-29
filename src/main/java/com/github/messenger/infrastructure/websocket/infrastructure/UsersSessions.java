package com.github.messenger.infrastructure.websocket.infrastructure;

import com.github.messenger.domain.value_objects.UserId;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class UsersSessions {
    private final ConcurrentMap<UserId, ClientConnections> sessionsByUser;
    private final ConcurrentMap<String, UserId> userBySession;

    public UsersSessions() {
        sessionsByUser = new ConcurrentHashMap<>();
        userBySession = new ConcurrentHashMap<>();
    }

    public void removeUserSessions(UserId userId) {
        ClientConnections clientConnections = sessionsByUser.remove(userId);

        if (clientConnections != null) {
            for (ClientConnection clientConnection : clientConnections.getConnections()) {
                userBySession.remove(clientConnection.getId());
            }
        }
    }

    // Удаляет сессию пользователя и возвращает последняя ли сессия была удалена
    public boolean removeWebSocketSession(String sessionId) {
        UserId userId = userBySession.get(sessionId);

        if (userId != null) {
            userBySession.remove(sessionId);

            ClientConnections connections = sessionsByUser.get(userId);
            if (connections != null) {
                connections.closeConnection(sessionId);

                // если пользователь вышел из всех ws сессий, то удаляем ключ
                if (connections.isEmpty()) {
                    sessionsByUser.remove(userId);
                    return true;
                }
            }
        }
        return false;
    }

    public ClientConnection addWebSocketSession(UserId userId, WebSocketSession session) {
        ClientConnection clientConnection = new ClientConnection(session);

        sessionsByUser
                .computeIfAbsent(userId, k -> new ClientConnections())
                .addConnection(clientConnection);

        userBySession.put(session.getId(), userId);
        return clientConnection;
    }

    public UserId getUserId(String sessionId) {
        return userBySession.get(sessionId);
    }

    public @Nullable ClientConnections getSession(UserId userId) {
        return sessionsByUser.get(userId);
    }
}
