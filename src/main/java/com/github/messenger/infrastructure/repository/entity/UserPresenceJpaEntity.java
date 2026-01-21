package com.github.messenger.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="user_presence")
@Data
public class UserPresenceJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long lastSeen = 0L;

    @Column(nullable = false)
    private Boolean isOnline = false;

    public UserPresenceJpaEntity(Long lastSeen, Boolean isOnline) {
        this.lastSeen = lastSeen;
        this.isOnline = isOnline;
    }
    public UserPresenceJpaEntity() {}

}
