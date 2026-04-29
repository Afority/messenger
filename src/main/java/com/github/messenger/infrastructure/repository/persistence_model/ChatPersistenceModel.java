package com.github.messenger.infrastructure.repository.persistence_model;

import com.github.messenger.infrastructure.repository.entity.ChatJpaEntity;
import com.github.messenger.infrastructure.repository.entity.ChatMemberJpaEntity;
import lombok.Getter;

import java.util.List;

@Getter
public class ChatPersistenceModel {
    private ChatJpaEntity chat;
    private List<ChatMemberJpaEntity> members;

    public ChatPersistenceModel(ChatJpaEntity chat, List<ChatMemberJpaEntity> members) {
        this.chat = chat;
        this.members = members;
    }
}
