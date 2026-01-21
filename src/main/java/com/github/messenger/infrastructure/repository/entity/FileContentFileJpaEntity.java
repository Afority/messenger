package com.github.messenger.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "file_content_files",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"file_content_id", "file_id"}
        ))
@Data
public class FileContentFileJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "file_content_id")
    private FileContentJpaEntity fileContent;

    @ManyToOne(optional = false)
    @JoinColumn(name = "file_id")
    private FileJpaEntity file;

    private Integer orderIndex;
}

