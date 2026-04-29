package com.github.messenger.domain.value_objects;

import org.jetbrains.annotations.NotNull;

public record UserId (long value) {
    public UserId {
        if (value < 0) throw new IllegalArgumentException("id cannot be negative");
    }

    @Override
    public @NotNull String toString() {
        return Long.toString(value);
    }
}
