package com.github.messenger.domain.events_system.event_messages;

import com.github.messenger.domain.entity.Message;
import com.github.messenger.domain.events_system.EventMessage;

public record MessageSentEventMessage(Message message, int userEventId) implements EventMessage {
}
