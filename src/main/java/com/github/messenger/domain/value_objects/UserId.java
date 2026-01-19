package com.github.messenger.domain.value_objects;

import lombok.Getter;

public class UserId {
    @Getter
    private final long value;

    public UserId(long value) {
        if (value < 0) throw new IllegalArgumentException("id cannot be negative");
        this.value = value;
    }
}
