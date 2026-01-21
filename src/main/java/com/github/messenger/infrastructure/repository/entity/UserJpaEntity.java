package com.github.messenger.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class UserJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(nullable = false)
    private String password;

    @OneToOne
    private UserPresenceJpaEntity presence;

    public UserJpaEntity(Long id, String login, String password, UserPresenceJpaEntity presence) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.presence = presence;
    }

    public UserJpaEntity(String login, String password, UserPresenceJpaEntity presence) {
        this.login = login;
        this.password = password;
        this.presence = presence;
    }

    public UserJpaEntity() {}
}

