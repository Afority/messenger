package com.github.messenger.domain.entity;

import com.github.messenger.domain.value_objects.ChatId;
import com.github.messenger.domain.value_objects.MessageId;
import com.github.messenger.domain.value_objects.MessageNumber;
import com.github.messenger.domain.value_objects.UserId;

import java.time.Instant;

public sealed interface Message
        permits TextMessage, ServiceMessage {

    MessageId id();
    MessageNumber number();
    UserId sender();
    ChatId chat();
    Instant sentAt();
}

