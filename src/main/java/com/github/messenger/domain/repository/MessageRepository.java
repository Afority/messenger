package com.github.messenger.domain.repository;

import com.github.messenger.domain.entity.Message;
import com.github.messenger.domain.value_objects.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface MessageRepository {
    Message saveUserMessage(
        UserId senderId,
        ChatId chatId,
        TextContent textContent,
        List<FileId> attachments,
        Instant sendingTime
    );

    List<Message> getMessages(ChatId chatId, long messageNumberStart, long limit);

    List<Message> getMessages(List<MessageNumber> messagesNumbers, ChatId chatId);

    Optional<Message> getMessage(MessageNumber messagesNumber, ChatId chatId);

    Optional<Message> getMessage(MessageId messageId);
}
