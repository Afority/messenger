package com.github.messenger.domain.value_objects;

public record MessageNumber(long value) {
    public MessageNumber {
        if (value < 0) {
            throw new IllegalArgumentException("message number cannot be negative");
        }
    }
}
