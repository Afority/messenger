package com.github.messenger.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users_in_chat",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "chat_id"}))
@Data
public class UserInChatJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private UserJpaEntity user;

    @ManyToOne(optional = false)
    private ChatJpaEntity chat;
}


