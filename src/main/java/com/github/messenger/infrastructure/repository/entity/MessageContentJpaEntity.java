package com.github.messenger.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name="message_content")
public class MessageContentJpaEntity {
    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "message_id")
    private MessageJpaEntity message;

    @Column(columnDefinition = "TEXT")
    private String text;

    public MessageContentJpaEntity() {
    }

    public MessageContentJpaEntity(MessageJpaEntity message, String text) {
        this.message = message;
        this.text = text;
    }
}
