package com.github.messenger.domain.value_objects;

public record MessageId (long value) {
    public MessageId {
        if (value < 0) throw new IllegalArgumentException("id cannot be negative");
    }
}
