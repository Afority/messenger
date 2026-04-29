package com.github.messenger.infrastructure.websocket.controllers.events;

import com.github.messenger.domain.entity.Attachment;

import java.util.List;

public class SentMessageEvent extends BasicEventMessage {

    private record Payload(
            long senderId,
            String chatId,
            String messageText,
            List<Attachment> attachments,
            long sendingTime,
            long messageNumber
    ) {}

    public SentMessageEvent(int userEventId,
                            long senderId,
                            String chatId,
                            String messageText,
                            List<Attachment> attachments,
                            long time,
                            long messageNumber) {
        super(userEventId, 3, new Payload(senderId, chatId, messageText, attachments, time, messageNumber));
    }
}
