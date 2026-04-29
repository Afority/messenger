package com.github.messenger.infrastructure.repository.adapter;


import com.github.messenger.domain.exceptions.MessageNotFoundException;
import com.github.messenger.domain.exceptions.UserNotFoundException;
import com.github.messenger.domain.repository.MessageViewRepository;
import com.github.messenger.domain.value_objects.ChatId;
import com.github.messenger.domain.value_objects.MessageId;
import com.github.messenger.domain.value_objects.MessageNumber;
import com.github.messenger.domain.value_objects.UserId;
import com.github.messenger.infrastructure.repository.MessageJpaRepository;
import com.github.messenger.infrastructure.repository.MessageViewJpaRepository;
import com.github.messenger.infrastructure.repository.UserJpaRepository;
import com.github.messenger.infrastructure.repository.entity.MessageJpaEntity;
import com.github.messenger.infrastructure.repository.entity.MessageViewJpaEntity;
import com.github.messenger.infrastructure.repository.entity.UserJpaEntity;
import com.github.messenger.infrastructure.repository.mapper.MessageMapper;
import com.github.messenger.infrastructure.repository.return_types.MessageViewRow;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MessageViewRepositoryAdapter implements MessageViewRepository {
    private final MessageViewJpaRepository messageViewJpaRepository;
    private final MessageJpaRepository messageRepository;
    private final UserJpaRepository userRepository;
    private final MessageMapper messageMapper;

    public MessageViewRepositoryAdapter(MessageViewJpaRepository messageViewRepository,
                                        MessageJpaRepository messageRepository,
                                        UserJpaRepository userRepository,
                                        MessageMapper messageMapper) {
        this.messageViewJpaRepository = messageViewRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.messageMapper = messageMapper;
    }

    @Override
    public void setMark(List<MessageId> messagesIds, UserId userId) {
        UserJpaEntity user = userRepository
            .findById(userId.value())
            .orElseThrow(UserNotFoundException::new);

        for (MessageId messageId : messagesIds) {
            try {
                messageViewJpaRepository.save(
                    new MessageViewJpaEntity(new MessageJpaEntity(messageId.value()), user)
                );
            }
            catch (DataIntegrityViolationException ignored) { }
        }
    }

    @Override
    public void setMark(List<MessageNumber> messagesNumbers, ChatId chatId, UserId userId) {
        UserJpaEntity user = userRepository
            .findById(userId.value())
            .orElseThrow(UserNotFoundException::new);

        for (MessageNumber messageNumber : messagesNumbers) {
            MessageJpaEntity message = messageRepository
                .findByMessageNumberAndChatId(messageNumber.value(), chatId.value())
                .orElseThrow(MessageNotFoundException::new);

            try {
                messageViewJpaRepository.save(new MessageViewJpaEntity(message, user));
            }
            catch (DataIntegrityViolationException ignored) { }
        }
    }

    @Override
    public List<UserId> getViewers(MessageNumber number, ChatId chatId) {
        return List.of();
    }

    @Override
    public List<UserId> getViewers(MessageId messageId) {
        return List.of();
    }

    @Override
    @Transactional
    public Map<MessageId, List<UserId>> getViewers(List<MessageId> messageId) {
        List<MessageViewRow> messages =
            messageViewJpaRepository.findByMessageIdIn(messageId.stream().map(MessageId::value).toList());

        return messages.stream()
            .collect(Collectors.groupingBy(
                messageViewRow -> new MessageId(messageViewRow.messageId()),
                Collectors.mapping(messageViewRow -> new UserId(messageViewRow.userId()), Collectors.toList()))
            );
    }
}
