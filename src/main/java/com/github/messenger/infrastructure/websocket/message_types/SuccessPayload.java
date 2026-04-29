package com.github.messenger.infrastructure.websocket.message_types;

public class SuccessPayload extends BasicPayload {
    public SuccessPayload() {
        super(true);
    }
    public SuccessPayload(String message) {
        super(true,  message);
    }
}

