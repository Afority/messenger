package com.github.messenger.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "message_views",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "chat_id"}
        ))
@Data
public class MessageViewJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private UserJpaEntity user;

    @ManyToOne(optional = false)
    private ChatJpaEntity chat;

    @ManyToOne(optional = false)
    @JoinColumn(name = "last_message_id")
    private MessageJpaEntity lastReadMessage;
}
