package com.github.messenger.infrastructure.websocket.infrastructure;

import tools.jackson.databind.json.JsonMapper;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ClientConnections implements UserCommunication {
    private final Set<ClientConnection> connections;
    private final JsonMapper jsonMapper;

    public ClientConnections() {
        connections = Collections.synchronizedSet(new HashSet<>());
        jsonMapper = new JsonMapper();
    }

    public void addConnection(ClientConnection connection) {
        connections.add(connection);
    }

    public Set<ClientConnection> getConnections() {
        return Collections.unmodifiableSet(connections);
    }

    @Override
    public void registerMessageForSession(String sessionId, String message) {
        for (ClientConnection connection : connections) {
            if (connection.getId().equals(sessionId)) {
                connection.registerMessage(message);
                return;
            }
        }
    }

    @Override
    public void registerMessageForSession(String sessionId, Object jsonObject) {
        registerMessageForSession(sessionId, jsonMapper.writeValueAsString(jsonObject));
    }

    @Override
    public void enqueueForUserSessionsExcept(String excludedSessionId, String message) {
        for (ClientConnection connection : connections) {
            if (!connection.getId().equals(excludedSessionId)) {
                connection.registerMessage(message);
            }
        }
    }

    @Override
    public void enqueueForUserSessionsExcept(String excludedSessionId, Object jsonObject) {
        enqueueForUserSessionsExcept(excludedSessionId, jsonMapper.writeValueAsString(jsonObject));
    }

    @Override
    public void registerMessage(String message) {
        for (ClientConnection connection : connections) {
            connection.registerMessage(message);
        }
    }

    @Override
    public void registerMessage(Object jsonObject) {
        registerMessage(jsonMapper.writeValueAsString(jsonObject));
    }

    public void closeConnection(String sessionId) {
        for (ClientConnection connection : connections) {
            if (connection.getId().equals(sessionId)) {
                connection.close();
                return;
            }
        }
    }

    public boolean isEmpty() {
        return connections.isEmpty();
    }
}
