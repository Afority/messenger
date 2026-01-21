package com.github.messenger.domain.entity;

import com.github.messenger.domain.value_objects.MessageId;
import com.github.messenger.domain.value_objects.UserId;

import java.time.Instant;
import java.util.UUID;

public sealed interface Message
        permits TextMessage, FileMessage, EventMessage {

    MessageId id();
    UserId sender();
    UUID chat();
    Instant sentAt();
}

