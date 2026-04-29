package com.github.messenger.infrastructure.websocket.controllers.events;

import com.github.messenger.infrastructure.websocket.message_types.MessageType;
import lombok.Data;

/*
{
    "type": "EVENT",
    "eventId": int,
    "opcode": int,
    "payload": {
        ...
    }
*/
@Data
public class BasicEventMessage {
    private MessageType type;
    private int userEventId;
    private int opcode;
    private Object payload;

    protected BasicEventMessage(int eventId, int opcode, Object payload) {
        this.type = MessageType.EVENT;
        this.userEventId = eventId;
        this.opcode = opcode;
        this.payload = payload;
    }
}
