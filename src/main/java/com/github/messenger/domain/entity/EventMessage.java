package com.github.messenger.domain.entity;

import com.github.messenger.domain.value_objects.EventType;
import com.github.messenger.domain.value_objects.MessageId;
import com.github.messenger.domain.value_objects.UserId;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class EventMessage implements Message {
    private MessageId messageId;
    private UserId senderId;
    private UUID chatId;
    private EventType type;
    private Instant sendingTime;

    public EventMessage(MessageId messageId, UserId senderId, UUID chatId, EventType type, Instant sendingTime) {
        Objects.requireNonNull(messageId, "messageId cannot be null");
        Objects.requireNonNull(senderId, "senderId cannot be null");
        Objects.requireNonNull(chatId, "chatId cannot be null");
        Objects.requireNonNull(type,  "type cannot be null");

        this.messageId = messageId;
        this.senderId = senderId;
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
    public UUID chat() {
        return chatId;
    }

    @Override
    public Instant sentAt() {
        return sendingTime;
    }

    public EventType type() {
        return type;
    }
}
