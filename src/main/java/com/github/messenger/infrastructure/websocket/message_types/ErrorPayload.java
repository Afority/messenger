package com.github.messenger.infrastructure.websocket.message_types;

public class ErrorPayload extends BasicPayload {
    public ErrorPayload(String message) {
        super(false, message);
    }
}