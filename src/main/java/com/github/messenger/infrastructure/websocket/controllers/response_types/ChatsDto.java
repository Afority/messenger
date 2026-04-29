package com.github.messenger.infrastructure.websocket.controllers.response_types;

import org.jetbrains.annotations.Nullable;
import com.github.messenger.infrastructure.web.dtos.UserDto;

import java.util.List;

public record ChatsDto(
        String chatId,
        String type,
        List<UserDto> participants,
        Long lastMessageNumber,
        Long unreadMessages,
        MessagePreviewDto lastMessage,
        @Nullable String name,
        @Nullable String description,
        @Nullable String photoId
) {}
