package com.github.messenger.infrastructure.repository.adapter.dtos;

import com.github.messenger.domain.entity.Chat;
import com.github.messenger.domain.entity.User;
import com.github.messenger.domain.value_objects.FileId;

import java.util.List;

public record GetChatsResult(
        Chat chat,
        List<User> participants,
        MessagePreview lastMessage,
        long unreadMessages
){
}
