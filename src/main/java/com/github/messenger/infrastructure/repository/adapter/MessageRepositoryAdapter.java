package com.github.messenger.infrastructure.repository.adapter;

import com.github.messenger.domain.entity.Message;
import com.github.messenger.domain.exceptions.FileNotFoundException;
import com.github.messenger.domain.exceptions.MessageNotFoundException;
import com.github.messenger.domain.exceptions.UserNotFoundException;
import com.github.messenger.domain.repository.MessageRepository;
import com.github.messenger.domain.value_objects.*;
import com.github.messenger.infrastructure.repository.*;
import com.github.messenger.infrastructure.repository.entity.*;
import com.github.messenger.infrastructure.repository.mapper.MessageMapper;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Component
public class MessageRepositoryAdapter implements MessageRepository {
    private final MessageJpaRepository messageRepository;
    private final MessageMapper messageMapper;
    private final ChatCounterRepository chatCounterRepository;
    private final MessageViewJpaRepository messageViewJpaRepository;
    private final UserJpaRepository userRepository;
    private final MessageAttachmentRepository messageAttachmentRepository;
    private final FilesJpaRepository fileRepository;

    public MessageRepositoryAdapter(
        MessageJpaRepository messageRepository,
        MessageMapper messageMapper,
        ChatCounterRepository chatCounterRepository,
        MessageViewJpaRepository messageViewJpaRepository,
        UserJpaRepository userRepository,
        MessageAttachmentRepository messageAttachmentRepository,
        FilesJpaRepository fileRepository) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
        this.chatCounterRepository = chatCounterRepository;
        this.messageViewJpaRepository = messageViewJpaRepository;
        this.userRepository = userRepository;
        this.messageAttachmentRepository = messageAttachmentRepository;
        this.fileRepository = fileRepository;
    }

    @Override
    @Transactional
    public Message saveUserMessage(
        UserId senderId,
        ChatId chatId,
        @Nullable TextContent textContent,
        List<FileId> attachments,
        Instant sendingTime) {
        long nextMessageNumber = chatCounterRepository.nextMessageNumber(chatId.value());

        MessageJpaEntity message = new MessageJpaEntity(
            nextMessageNumber,
            ContentType.TEXT,
            new UserJpaEntity(senderId.value()),
            new ChatJpaEntity(chatId.value()),
            textContent == null ? null : textContent.getText(),
            sendingTime.getEpochSecond()
        );
        MessageJpaEntity savedMessage = messageRepository.save(message);

        for (FileId attachment : attachments) {
            FileJpaEntity file = fileRepository
                .findById(attachment.id())
                .orElseThrow(() -> new FileNotFoundException("file not found"));

            messageAttachmentRepository.save(
                new MessageAttachmentJpaEntity(
                    new MessageAttachmentId(message.getId(), file.getId()), message, file
                )
            );
        }

        return messageMapper.toDomain(savedMessage, savedMessage.getAttachments());
    }

    @Override
    @Transactional // для ленивой загрузки вложений и просмотра сообщений
    public List<Message> getMessages(ChatId chatId, long messageNumberStart, long limit) {
        List<MessageJpaEntity> messages = messageRepository.getMessagesByNumberAndChatId(
            chatId.value(),
            messageNumberStart, limit
        );

        return messages.stream()
            .map(message -> messageMapper.toDomain(message, message.getAttachments()))
            .toList();
    }

    @Override
    @Transactional // для ленивой загрузки вложений и просмотра сообщений
    public List<Message> getMessages(List<MessageNumber> messagesNumbers, ChatId chatId) {
        List<MessageJpaEntity> messages = messageRepository.getMessagesByNumbersAndChatId(
            messagesNumbers.stream()
                .map(MessageNumber::value)
                .toList(),
            chatId.value()
        );

        return messages.stream()
            .map(message -> messageMapper.toDomain(message, message.getAttachments()))
            .toList();
    }

    @Override
    @Transactional // для ленивой загрузки вложений и просмотра сообщений
    public Optional<Message> getMessage(MessageNumber messagesNumber, ChatId chatId) {
        Optional<MessageJpaEntity> messageOptional =
            messageRepository.findByMessageNumberAndChatId(
                messagesNumber.value(),
                chatId.value()
            );

        if (messageOptional.isEmpty()) {
            return Optional.empty();
        }

        return messageOptional.map(message -> messageMapper.toDomain(message, message.getAttachments()));
    }

    @Override
    @Transactional // для ленивой загрузки вложений и просмотра сообщений
    public Optional<Message> getMessage(MessageId messageId) {
        Optional<MessageJpaEntity> messageOptional =
            messageRepository.findById(
                messageId.value()
            );

        return messageOptional.map(message -> messageMapper.toDomain(message, message.getAttachments()));
    }
}
