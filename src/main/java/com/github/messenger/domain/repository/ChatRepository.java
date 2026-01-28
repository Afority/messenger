package com.github.messenger.domain.repository;

import com.github.messenger.domain.entity.PersonalChat;
import com.github.messenger.domain.value_objects.ChatId;
import com.github.messenger.domain.value_objects.UserId;

import java.util.List;

public interface ChatRepository {
    ChatId savePersonalChat(List<UserId> members);
    PersonalChat findPersonalChat(ChatId chatId);
}
