package com.github.messenger.domain.usecase;

import com.github.messenger.domain.entity.GroupChat;
import com.github.messenger.domain.entity.PersonalChat;
import com.github.messenger.domain.exceptions.UserNotFoundException;
import com.github.messenger.domain.repository.ChatRepository;
import com.github.messenger.domain.repository.UserRepository;
import com.github.messenger.domain.value_objects.ChatId;
import com.github.messenger.domain.value_objects.UserId;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreateChatUseCase {
    ChatRepository chatRepository;
    UserRepository userRepository;

    public CreateChatUseCase(ChatRepository chatRepository, UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    public ChatId createPersonalChat(List<UserId> members) {
        if (members.size() != 2)
            throw new IllegalStateException("В персональном чате участников должно быть 2");

        for (UserId userId : members) {
            if (!userRepository.existsById(userId)) {
                throw new UserNotFoundException(userId);
            }
        }
        return chatRepository.savePersonalChat(members);
    }
}
