package com.github.messenger.domain.entity;

import com.github.messenger.domain.value_objects.FileContent;
import com.github.messenger.domain.value_objects.MessageId;
import com.github.messenger.domain.value_objects.UserId;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class FileMessage implements Message {
    private MessageId messageId;
    private UserId senderId;
    private UUID chatId;
    private List<FileContent> content;
    private Instant sendingTime;

    public FileMessage(MessageId messageId, UserId senderId, UUID chatId, List<FileContent> content, Instant sendingTime) {
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

    public List<FileContent> content() {
        return content;
    }
}
