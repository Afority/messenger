package com.github.messenger.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "chats")
@Data
public class ChatJpaEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "photo_id")
    private FileJpaEntity photo;
}


