package com.github.messenger.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "files")
@Data
public class FileJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String filename;
    private boolean isDone;

    public FileJpaEntity(UUID id) {
        this.id = id;
    }

    public FileJpaEntity(UUID id, String filename, boolean isDone) {
        this.id = id;
        this.filename = filename;
        this.isDone = isDone;
    }

    public FileJpaEntity() {
    }
}
