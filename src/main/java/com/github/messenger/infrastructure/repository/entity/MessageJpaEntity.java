package com.github.messenger.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "messages")
@Data
public class MessageJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private UserJpaEntity sender;

    @Column(nullable = false)
    private Long sendingTime;

    @ManyToOne(optional = false)
    private ChatJpaEntity chat;

    @OneToOne(optional = false)
    @JoinColumn(name = "content_id")
    private ContentJpaEntity content;

    private boolean isEdited;
    private Long lastEditedTime;
}


