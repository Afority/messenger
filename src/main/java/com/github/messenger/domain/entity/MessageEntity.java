package com.github.messenger.domain.entity;

import com.github.messenger.domain.value_objects.Message;
import com.github.messenger.domain.value_objects.MessageContent;
import com.github.messenger.domain.value_objects.MessageId;
import com.github.messenger.domain.value_objects.UserId;
import lombok.Getter;

import java.util.Objects;

@Getter
public class MessageEntity {
    private MessageId messageId;
    private UserId senderId;
    private MessageContent content;
    private long sendingTime;

    public MessageEntity(MessageId messageId, UserId senderId, MessageContent content, long sendingTime) {
        Objects.requireNonNull(messageId, "messageId cannot be null");
        Objects.requireNonNull(senderId, "senderId cannot be null");
        Objects.requireNonNull(content,  "content cannot be null");

        if (sendingTime < 0)
            throw new IllegalArgumentException("timestamp is negative");

        this.messageId = messageId;
        this.senderId = senderId;
        this.content = content;
        this.sendingTime = sendingTime;
    }
}
