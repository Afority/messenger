package com.github.messenger.infrastructure.websocket.controllers.response_types;

import com.github.messenger.infrastructure.websocket.message_types.SuccessPayload;
import lombok.Getter;

public class EventResponse extends SuccessPayload {
    @Getter
    private final int eventId;

    public EventResponse(int eventId) {
        this.eventId = eventId;
    }
}
