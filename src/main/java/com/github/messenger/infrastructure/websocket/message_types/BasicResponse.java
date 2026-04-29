package com.github.messenger.infrastructure.websocket.message_types;

import lombok.Data;

/*
{
    "requestId": 1,
    "type": "RESPONSE",
    "payload": {
        "success": true/false,
        при ошибке добавляется её описание:
            "message": ""
        ...
    }
}
*/

@Data
public class BasicResponse {
    protected int requestId;
    protected MessageType type;
    protected Object payload;

    public BasicResponse(int requestId, Object payload) {
        this.requestId = requestId;
        this.type = MessageType.RESPONSE;
        this.payload = payload;
    }
}
