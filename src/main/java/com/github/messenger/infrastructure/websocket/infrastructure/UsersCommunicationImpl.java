package com.github.messenger.infrastructure.websocket.infrastructure;

import com.github.messenger.domain.value_objects.UserId;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class UsersCommunicationImpl implements UsersCommunication {
    private final UsersSessions usersSessions;
    private final JsonMapper jsonMapper;

    public UsersCommunicationImpl(UsersSessions usersSessions) {
        this.usersSessions = usersSessions;
        this.jsonMapper = new JsonMapper();
    }

    @Override
    public void enqueueForUserSessions(UserId userId, String messageText) {
        ClientConnections sessions = usersSessions.getSession(userId);

        if (sessions != null) {
            sessions.registerMessage(messageText);
        }
    }

    @Override
    public void enqueueForUserSessions(UserId userId, Object dto) {
        enqueueForUserSessions(userId, jsonMapper.writeValueAsString(dto));
    }

    @Override
    public void enqueueForUserSessions(UserId userId, String message, String exceptedUserSessionId) {
        ClientConnections sessions = usersSessions.getSession(userId);

        if (sessions != null) {
            sessions.enqueueForUserSessionsExcept(exceptedUserSessionId, message);
        }
    }

    @Override
    public void enqueueForUserSessions(UserId userId, Object jsonObject, String exceptedUserSessionId) {
        enqueueForUserSessions(userId, jsonMapper.writeValueAsString(jsonObject), exceptedUserSessionId);
    }
}
