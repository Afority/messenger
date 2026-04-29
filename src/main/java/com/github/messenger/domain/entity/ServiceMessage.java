package com.github.messenger.domain.entity;

import com.github.messenger.domain.value_objects.*;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

public final class ServiceMessage implements Message {
    private MessageId messageId;
    private MessageNumber messageNumber;
    private UserId senderId;
    private ChatId chatId;
    @Getter
    private EventType type;
    private Instant sendingTime;

    public ServiceMessage(MessageId messageId,
                          MessageNumber messageNumber,
                          UserId senderId,
                          ChatId chatId,
                          EventType type,
                          Instant sendingTime) {
        Objects.requireNonNull(messageId, "messageId cannot be null");
        Objects.requireNonNull(messageNumber, "messageNumber cannot be null");
        Objects.requireNonNull(senderId, "senderId cannot be null");
        Objects.requireNonNull(chatId, "chatId cannot be null");
        Objects.requireNonNull(type,  "type cannot be null");

        this.messageId = messageId;
        this.senderId = senderId;
        this.messageNumber = messageNumber;
        this.chatId = chatId;
        this.type = type;
        this.sendingTime = sendingTime;
    }

    @Override
    public MessageId id() {
        return messageId;
    }

    @Override
    public UserId sender() {
        return senderId;
    }

    @Override
    public ChatId chat() {
        return chatId;
    }

    @Override
    public Instant sentAt() {
        return sendingTime;
    }

    @Override
    public MessageNumber number() {
        return messageNumber;
    }
}
