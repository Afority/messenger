package com.github.messenger.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;


@Entity
@Table(name = "messages")
@Getter
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

    public MessageJpaEntity() {
    }

    public MessageJpaEntity(Long messageNumber, UserJpaEntity sender, ChatJpaEntity chat, long sendingTime, ContentType contentType) {
        this.messageNumber = messageNumber;
        this.sender = sender;
        this.chat = chat;
        this.sendingTime = sendingTime;
        this.contentType = contentType;
    }
}
