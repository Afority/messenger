package com.github.messenger.infrastructure.repository.entity;

import jakarta.persistence.*;


@Entity
@Table(name = "messages")
public class MessageJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_number")
    private Long messageNumber;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private UserJpaEntity sender;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private ChatJpaEntity chat;

    private Long sendingTime;

    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    private boolean edited;
    private Long lastEditedTime;
}
