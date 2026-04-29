package com.github.messenger.domain.events_system.event_messages;

import com.github.messenger.domain.events_system.EventMessage;
import com.github.messenger.domain.value_objects.ChatId;
import com.github.messenger.domain.entity.User;
import com.github.messenger.domain.value_objects.ChatType;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record ChatCreatedEventMessage(
        List<User> participants,
        ChatId chatId,
        ChatType chatType,
        @Nullable String chatName,
        @Nullable String chatDescription,
        int userEventId
) implements EventMessage { }
