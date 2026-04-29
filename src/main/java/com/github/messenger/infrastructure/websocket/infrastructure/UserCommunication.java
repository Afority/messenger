package com.github.messenger.infrastructure.websocket.infrastructure;

// Интерфейс для общения с пользователем
public interface UserCommunication {
    // регистрирует сообщение конкретной WS сессии
    void registerMessageForSession(String sessionId, String message);
    void registerMessageForSession(String sessionId, Object jsonObject);

    // регистрирует сообщение для всех WS сессий
    void registerMessage(String message);
    void registerMessage(Object jsonObject);

    // регистрирует сообщение для всех WS сессий, кроме переданного sessionId
    void enqueueForUserSessionsExcept(String excludedSessionId, String message);
    void enqueueForUserSessionsExcept(String excludedSessionId, Object jsonObject);
}
