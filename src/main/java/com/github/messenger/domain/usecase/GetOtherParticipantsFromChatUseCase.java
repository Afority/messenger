package com.github.messenger.domain.usecase;

import com.github.messenger.domain.entity.PersonalChat;
import com.github.messenger.domain.exceptions.ChatNotFoundException;
import com.github.messenger.domain.repository.ChatRepository;
import com.github.messenger.domain.value_objects.ChatId;
import com.github.messenger.domain.value_objects.UserId;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class GetOtherParticipantsFromChatUseCase {
    ChatRepository chatRepository;

    public GetOtherParticipantsFromChatUseCase(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public List<UserId> execute(ChatId chatId, UserId requesterId) {
        Optional<PersonalChat> chat = chatRepository.findPersonalChat(chatId);

        return chat.map(
    personalChat -> personalChat.getParticipants()
                .stream()
                .filter(userId -> userId.value() != requesterId.value())
                .toList()
        ).orElseThrow(ChatNotFoundException::new);
    }
}
