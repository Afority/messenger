package com.github.messenger.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="messages_views")
@Data
public class MessageViewJpaEntity {
    @EmbeddedId
    private MessageViewIdJpaEntity id;

    @ManyToOne
    @JoinColumn(name="message_id", nullable = false)
    @MapsId("messageId")
    private MessageJpaEntity message;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @MapsId("userId")
    private UserJpaEntity user;

    public MessageViewJpaEntity() {}

    public MessageViewJpaEntity(MessageJpaEntity message, UserJpaEntity user) {
        this.id = new MessageViewIdJpaEntity(user.getId(), message.getId());
        this.message = message;
        this.user = user;
    }
}
