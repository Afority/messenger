package com.github.messenger.domain.entity;

import com.github.messenger.domain.value_objects.Login;
import com.github.messenger.domain.value_objects.UserId;
import com.github.messenger.domain.value_objects.Username;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

@Getter
public class User {
    private UserId id;

    private Login login;
    private Username username;

    // hashed
    private String password;

    private boolean isOnline;

    private Instant lastSeen;

    public User(UserId id, Login login, Username username,
                String password, boolean isOnline, Instant lastSeen) {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(login, "login cannot be null");
        Objects.requireNonNull(username, "username cannot be null");
        Objects.requireNonNull(password, "password cannot be null");
        Objects.requireNonNull(lastSeen, "lastSeen cannot be null");

        this.id = id;
        this.login = login;
        this.username = username;
        this.password = password;
        this.isOnline = isOnline;
        this.lastSeen = lastSeen;
    }
}
