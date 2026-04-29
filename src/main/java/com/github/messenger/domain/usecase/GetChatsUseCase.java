package com.github.messenger.domain.usecase;

import com.github.messenger.domain.exceptions.UserNotFoundException;
import com.github.messenger.domain.repository.ChatRepository;
import com.github.messenger.domain.repository.UserRepository;
import com.github.messenger.infrastructure.repository.adapter.dtos.GetChatsResult;
import com.github.messenger.domain.value_objects.UserId;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetChatsUseCase {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    public GetChatsUseCase(ChatRepository chatRepository, UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    public List<GetChatsResult> execute(UserId userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        return chatRepository.getChats(userId);
    }
}
