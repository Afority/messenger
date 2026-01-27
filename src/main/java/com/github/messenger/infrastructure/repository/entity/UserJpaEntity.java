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

    private String login;
    private String password;
    private Long lastSeen = 0L;
    private boolean isOnline = false;

    public UserJpaEntity(Long id, String login, String password, Long lastSeen, boolean isOnline) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.lastSeen = lastSeen;
        this.isOnline = isOnline;
    }

    public UserJpaEntity(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public UserJpaEntity(Long id) {
        this.id = id;
    }

    public UserJpaEntity() {

    }
}
