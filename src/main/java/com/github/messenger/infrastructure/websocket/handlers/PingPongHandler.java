package com.github.messenger.infrastructure.websocket.handlers;

import com.github.messenger.domain.value_objects.UserId;
import com.github.messenger.infrastructure.websocket.infrastructure.OpcodeHandler;
import com.github.messenger.infrastructure.websocket.message_types.SuccessPayload;
import org.springframework.stereotype.Component;


@Component
public class PingPongHandler implements OpcodeHandler {
    @Override
    public int getOpcode() {
        return 0;
    }

    @Override
    public Object handleOpcode(Object jsonObj, UserId userId) {
        return new SuccessPayload("pong ёмаё");
    }
}

