package com.github.messenger.domain.value_objects;

import com.github.messenger.domain.exceptions.LoginIsInvalidException;
import lombok.Getter;

public class Login {
    @Getter
    private String value;

    public Login(String login) {
        if (isInvalid(login)) throw new LoginIsInvalidException();
        this.value = login;
    }

    public void changeLogin(String login){
        if (isInvalid(login)) throw new LoginIsInvalidException();
        this.value = login;
    }

    private static boolean isInvalid(String login) {
        return login.length() <= 3 || login.contains(" ");
    }
}
