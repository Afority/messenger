package com.github.messenger.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "files")
@Data
public class FileJpaEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String mimeType;

    @Column(nullable = false)
    private long size;

    public FileJpaEntity(UUID id, String filename, String mimeType, long size) {
        this.id = id;
        this.filename = filename;
        this.mimeType = mimeType;
        this.size = size;
    }

    public FileJpaEntity() {}
}
