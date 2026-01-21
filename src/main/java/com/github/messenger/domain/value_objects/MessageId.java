package com.github.messenger.domain.value_objects;

import lombok.Getter;

public record MessageId (long value) {
    public MessageId {
        if (value < 0) throw new IllegalArgumentException("id cannot be negative");
    }
}
