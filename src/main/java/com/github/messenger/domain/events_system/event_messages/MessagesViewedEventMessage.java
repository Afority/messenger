package com.github.messenger.domain.events_system.event_messages;

import com.github.messenger.domain.events_system.EventMessage;
import com.github.messenger.domain.value_objects.ChatId;
import com.github.messenger.domain.value_objects.MessageId;
import com.github.messenger.domain.value_objects.MessageNumber;
import com.github.messenger.domain.value_objects.UserId;

import java.util.List;

public record MessagesViewedEventMessage(
    List<MessageId> messages,
    List<MessageNumber> messagesNumbers,
    ChatId chatId,
    UserId viewedUser,
    long timestamp,
    int userEventId
) implements EventMessage
{ }
