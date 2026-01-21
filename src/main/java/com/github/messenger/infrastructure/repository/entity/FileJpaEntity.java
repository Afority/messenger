package com.github.messenger.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "files")
@Data
public class FileJpaEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String filename;

    private boolean isDone;
}


