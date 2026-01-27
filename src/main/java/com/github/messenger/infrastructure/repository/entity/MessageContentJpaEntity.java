package com.github.messenger.infrastructure.repository.entity;

import jakarta.persistence.*;

@Entity
public class MessageContentJpaEntity {
    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "message_id")
    private MessageJpaEntity message;

    @Column(columnDefinition = "TEXT")
    private String text;
}
