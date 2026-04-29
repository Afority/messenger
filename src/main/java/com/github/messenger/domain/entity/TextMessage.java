package com.github.messenger.domain.entity;

import com.github.messenger.domain.value_objects.*;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public final class TextMessage implements Message {
    private MessageId messageId;
    @Getter
    private MessageNumber messageNumber;
    private UserId senderId;
    private ChatId chatId;
    @Getter
    private @Nullable TextContent content;
    @Getter
    private List<Attachment> attachments;
    private Instant sendingTime;
    private @Nullable ForwardType forwardType;
    private @Nullable MessageId forwardedMessageId;

    public TextMessage(MessageId messageId,
                       MessageNumber messageNumber,
                       UserId senderId,
                       ChatId chatId,
                       @Nullable TextContent content,
                       List<Attachment> attachments,
                       Instant sendingTime) {
        Objects.requireNonNull(messageId, "messageId cannot be null");
        Objects.requireNonNull(messageNumber, "messageNumber cannot be null");
        Objects.requireNonNull(senderId, "senderId cannot be null");
        Objects.requireNonNull(chatId, "chatId cannot be null");
        Objects.requireNonNull(attachments, "attachments cannot be null");

        if (content == null && attachments.isEmpty()) {
            throw new IllegalArgumentException("content cannot be empty");
        }

        this.messageId = messageId;
        this.messageNumber = messageNumber;
        this.senderId = senderId;
        this.chatId = chatId;
        this.content = content;
        this.sendingTime = sendingTime;
        this.attachments = attachments;
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
