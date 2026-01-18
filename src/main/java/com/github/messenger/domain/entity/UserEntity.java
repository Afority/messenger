package com.github.messenger.domain.entity;

import com.github.messenger.domain.value_objects.Login;
import com.github.messenger.domain.value_objects.UserId;
import lombok.Getter;

public class UserEntity {
    @Getter
    private UserId id;

    @Getter
    private Login login;

    @Getter
    // hashed
    private String password;

    public UserEntity(UserId id, Login login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }
}
