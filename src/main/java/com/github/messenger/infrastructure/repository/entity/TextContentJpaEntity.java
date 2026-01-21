package com.github.messenger.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "text_content")
@Data
public class TextContentJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 4000)
    private String text;
}


