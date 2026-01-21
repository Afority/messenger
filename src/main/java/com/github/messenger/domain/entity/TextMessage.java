package com.github.messenger.domain.entity;

import com.github.messenger.domain.value_objects.MessageId;
import com.github.messenger.domain.value_objects.TextContent;
import com.github.messenger.domain.value_objects.UserId;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class TextMessage implements Message {
    private MessageId messageId;
    private UserId senderId;
    private UUID chatId;
    private TextContent content;
    private Instant sendingTime;

    public TextMessage(MessageId messageId, UserId senderId, UUID chatId, TextContent content, Instant sendingTime) {
        Objects.requireNonNull(messageId, "messageId cannot be null");
        Objects.requireNonNull(senderId, "senderId cannot be null");
        Objects.requireNonNull(chatId, "chatId cannot be null");
        Objects.requireNonNull(content,  "content cannot be null");

        this.messageId = messageId;
        this.senderId = senderId;
        this.chatId = chatId;
        this.content = content;
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

    public TextContent content() {
        return content;
    }
}
