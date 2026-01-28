package com.github.messenger.domain.repository;

import com.github.messenger.domain.entity.TextMessage;
import com.github.messenger.domain.value_objects.ChatId;
import com.github.messenger.domain.value_objects.TextContent;
import com.github.messenger.domain.value_objects.UserId;

import java.time.Instant;

public interface MessageRepository {
    TextMessage save(UserId senderId, ChatId chatId, TextContent textContent, Instant sendingTime);
}
