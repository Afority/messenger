package com.github.messenger.domain.usecase;

import com.github.messenger.domain.entity.TextMessage;
import com.github.messenger.domain.repository.MessageRepository;
import com.github.messenger.domain.value_objects.ChatId;
import com.github.messenger.domain.value_objects.TextContent;
import com.github.messenger.domain.value_objects.UserId;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class SendPrivateTextMessageUseCase {
    MessageRepository messageRepository;

    public SendPrivateTextMessageUseCase(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public TextMessage send(UserId senderId, ChatId chatId, TextContent textContent) {
        // todo сделать проверки
        return messageRepository.save(senderId, chatId, textContent, Instant.now());
    }
}
