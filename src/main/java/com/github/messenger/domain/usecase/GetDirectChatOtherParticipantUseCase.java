package com.github.messenger.domain.usecase;

import com.github.messenger.domain.entity.PersonalChat;
import com.github.messenger.domain.repository.ChatRepository;
import com.github.messenger.domain.value_objects.ChatId;
import com.github.messenger.domain.value_objects.UserId;
import org.springframework.stereotype.Component;

@Component
public class GetDirectChatOtherParticipantUseCase {
    ChatRepository chatRepository;

    public GetDirectChatOtherParticipantUseCase(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public UserId execute(ChatId chatId, UserId requesterId) {
        PersonalChat chat = chatRepository.findPersonalChat(chatId);
        // todo доделать
        return chat.getParticipants().stream()
                .filter(userId -> userId != requesterId)
                .findFirst()
                .get();
    }
}
