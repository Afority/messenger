package com.github.messenger.domain.value_objects;

import com.github.messenger.domain.exceptions.InvalidUserIdException;
import lombok.Getter;

public class UserId {
    @Getter
    private final long value;

    public UserId(long value) {
        if (value < 0) throw new InvalidUserIdException();
        this.value = value;
    }
}
