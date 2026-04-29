package com.github.messenger.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "chat_members")
@Data
public class ChatMemberJpaEntity {
    @EmbeddedId
    private ChatMemberIdJpaEntity id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private UserJpaEntity user;

    @ManyToOne
    @MapsId("chatId")
    @JoinColumn(name = "chat_id")
    private ChatJpaEntity chat;

    public ChatMemberJpaEntity(UserJpaEntity user, ChatJpaEntity chat) {
        id = new ChatMemberIdJpaEntity(user.getId(), chat.getId());
        this.user = user;
        this.chat = chat;
    }

    public ChatMemberJpaEntity() {
    }
}
