package com.github.messenger.infrastructure.websocket.message_types;

import lombok.Data;
import lombok.Getter;

/*
{
    "opcode": 1,
    "requestId": 1,
    "payload": {
        ...
    }
}
*/

@Data
public class BasicRequest {
    private int requestId;
    private int opcode;
    private Object payload;

    public BasicRequest(int requestId, int opcode, Object payload) {
        this.requestId = requestId;
        this.opcode = opcode;
        this.payload = payload;
    }
}
