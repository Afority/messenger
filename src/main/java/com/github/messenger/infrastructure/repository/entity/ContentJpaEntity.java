package com.github.messenger.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "content")
@Data
public class ContentJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payload_id")
    private Long payloadId; // ID в text_content / file_content

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentType type;
}


