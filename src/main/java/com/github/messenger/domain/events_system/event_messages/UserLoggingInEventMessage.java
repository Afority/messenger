package com.github.messenger.domain.events_system.event_messages;

import com.github.messenger.domain.events_system.EventMessage;
import com.github.messenger.domain.value_objects.UserId;

public record UserLoggingInEventMessage(UserId userId) implements EventMessage {
}
