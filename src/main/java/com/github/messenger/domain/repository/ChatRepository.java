package com.github.messenger.domain.repository;

import com.github.messenger.domain.entity.PersonalChat;
import com.github.messenger.domain.value_objects.ChatId;
import com.github.messenger.domain.value_objects.UserId;
import com.github.messenger.infrastructure.repository.adapter.dtos.GetChatsResult;

import java.util.List;
import java.util.Optional;

public interface ChatRepository {
    ChatId savePersonalChat(UserId creatorId, UserId participantId);
    ChatId saveGroupChat(List<UserId> participants, String name, String description);
    Optional<PersonalChat> findPersonalChat(ChatId chatId);
    boolean chatExists(ChatId chatId);
    List<GetChatsResult> getChats(UserId userId);
    boolean privateChatExists(UserId userId1, UserId userId2);
    boolean userInChat(UserId userId, ChatId chatId);
}
