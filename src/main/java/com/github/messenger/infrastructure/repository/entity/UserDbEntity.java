package com.github.messenger.infrastructure.repository.entity;

import com.github.messenger.domain.value_objects.Login;
import com.github.messenger.domain.value_objects.UserId;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="users")
public class UserDbEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String login;

    @Column(nullable = false)
    private String password;

    public UserDbEntity(UserId id, Login login, String hashedPassword) {
        this.id = id.getValue();
        this.login = login.getValue();
        this.password = hashedPassword;
    }

    public UserDbEntity(String login, String hashedPassword) {
        this.login = login;
        this.password = hashedPassword;
    }

    public UserDbEntity() {}
}
