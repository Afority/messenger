package com.github.messenger.domain.entity;

import com.github.messenger.domain.value_objects.ChatId;
import com.github.messenger.domain.value_objects.ChatType;
import com.github.messenger.domain.value_objects.UserId;

import java.util.List;

public sealed interface Chat permits PersonalChat, GroupChat {
    ChatId getChatId();
    List<UserId> getParticipants();
    long getLastMessageNumber();
    ChatType getType();
}
