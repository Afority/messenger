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

    @Column(unique = true, nullable = false)
    private String login;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @OneToOne(cascade = CascadeType.ALL)
    private FileJpaEntity file_id;

    private Long lastSeen = 0L;
    private boolean isOnline = false;

    public UserJpaEntity(Long id, String login, String username, String password, Long lastSeen, boolean isOnline) {
        this.id = id;
        this.login = login;
        this.username = username;
        this.password = password;
        this.lastSeen = lastSeen;
        this.isOnline = isOnline;
    }

    public UserJpaEntity(String login, String username, String password) {
        this.login = login;
        this.username = username;
        this.password = password;
    }

    public UserJpaEntity(Long id) {
        this.id = id;
    }

    public UserJpaEntity() {}
}
