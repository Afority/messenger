package com.github.messenger.domain.value_objects;

import com.github.messenger.domain.exceptions.UserNameIsInvalidException;
import lombok.Getter;

public class Username {
    @Getter
    private String usernameStr;

    public Username(String usernameStr) {
        validate(usernameStr);
        this.usernameStr = usernameStr;
    }

    private void validate(String usernameStr) {
        if (usernameStr == null || usernameStr.length() < 3) {
            throw new UserNameIsInvalidException();
        }
    }
}
