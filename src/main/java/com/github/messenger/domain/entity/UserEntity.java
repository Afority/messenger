package com.github.messenger.domain.entity;

import com.github.messenger.domain.value_objects.Login;
import com.github.messenger.domain.value_objects.UserId;
import lombok.Getter;

import java.util.Objects;

public class UserEntity {
    @Getter
    private UserId id;

    @Getter
    private Login login;

    @Getter
    // hashed
    private String password;

    public UserEntity(UserId id, Login login, String password) {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(login, "login cannot be null");
        Objects.requireNonNull(password, "password cannot be null");

        this.id = id;
        this.login = login;
        this.password = password;
    }
}
