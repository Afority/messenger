package com.github.messenger.infrastructure.websocket.infrastructure;

import com.github.messenger.domain.value_objects.UserId;

public interface UsersCommunication {
    // регистрирует сообщение для всех WS сессий
    void enqueueForUserSessions(UserId userId, String message);
    void enqueueForUserSessions(UserId userId, Object jsonObject);

    // регистрирует сообщение для всех WS сессий кроме переданного id сессии
    void enqueueForUserSessions(UserId userId, String message, String exceptedUserSessionId);
    void enqueueForUserSessions(UserId userId, Object jsonObject, String exceptedUserSessionId);
}
