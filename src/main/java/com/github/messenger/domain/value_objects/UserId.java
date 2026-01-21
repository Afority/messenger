package com.github.messenger.domain.value_objects;

public record UserId (long value) {
    public UserId {
        if (value < 0) throw new IllegalArgumentException("id cannot be negative");
    }
}
