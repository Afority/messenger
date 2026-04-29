package com.github.messenger.infrastructure.websocket.controllers.response_types;

import com.github.messenger.infrastructure.websocket.message_types.SuccessPayload;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserIdPayload extends SuccessPayload {
    private final long id;

    public UserIdPayload(long id) {
        this.id = id;
    }
}

