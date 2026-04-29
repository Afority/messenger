package com.github.messenger.domain.usecase;

import com.github.messenger.domain.entity.User;

import com.github.messenger.domain.events_system.EventBroker;
import com.github.messenger.domain.events_system.event_messages.ChatCreatedEventMessage;

import com.github.messenger.domain.exceptions.InvalidChatException;
import com.github.messenger.domain.exceptions.PrivateChatExistsException;
import com.github.messenger.domain.exceptions.UserNotFoundException;
import com.github.messenger.domain.exceptions.InvalidQuantityParticipants;

import com.github.messenger.domain.repository.ChatRepository;
import com.github.messenger.domain.repository.UserRepository;

import com.github.messenger.domain.value_objects.ChatId;
import com.github.messenger.domain.value_objects.ChatType;
import com.github.messenger.domain.value_objects.UserId;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CreateChatUseCase {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final EventBroker eventBroker;

    public CreateChatUseCase(ChatRepository chatRepository, UserRepository userRepository, EventBroker eventBroker) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.eventBroker = eventBroker;
    }

    public ChatId createPersonalChat(UserId firstMemberId, UserId secondMemberId, int userEventId) {
        if (firstMemberId.value() == secondMemberId.value()) {
            throw new InvalidChatException("Нельзя создать чат с самим собой");
        }

        User firstMember = userRepository.findById(firstMemberId)
                .orElseThrow(() -> new UserNotFoundException(firstMemberId));

        User secondMember = userRepository.findById(secondMemberId)
                .orElseThrow(() -> new UserNotFoundException(firstMemberId));

        if (chatRepository.privateChatExists(firstMemberId, secondMemberId)) {
            throw new PrivateChatExistsException("Чат уже существует");
        }

        ChatId chatId = chatRepository.savePersonalChat(firstMemberId, secondMemberId);

        eventBroker.publishEvent(
            new ChatCreatedEventMessage(
                List.of(firstMember, secondMember),
                chatId,
                ChatType.PERSONAL,
                null, null,
                userEventId
            )
        );

        return chatId;
    }

    public ChatId createGroupChat(List<UserId> participants, String name, String description, int userEventId) {
        List<UserId> uniqueParticipants = new ArrayList<>();

        for (UserId userId : participants) {
            if (!uniqueParticipants.contains(userId)) {
                uniqueParticipants.add(userId);
            }
        }

        if (uniqueParticipants.size() < 2) {
            throw new InvalidChatException("В чате должно быть минимум 2 участника");
        }
        for (UserId participantId : uniqueParticipants) {
            if (!userRepository.existsById(participantId)){
                throw new UserNotFoundException(participantId);
            }
        }

        ChatId chatId = chatRepository.saveGroupChat(uniqueParticipants, name, description);
        List<User> users = userRepository.findAllByIds(uniqueParticipants);

        eventBroker.publishEvent(
                new ChatCreatedEventMessage(
                    users,
                    chatId,
                    ChatType.GROUP,
                    name,
                    description,
                    userEventId
                )
        );
        return chatId;
    }
}
