package com.github.messenger.domain.value_objects;

import lombok.Getter;

public class MessageId {
    @Getter
    private final long value;

    public MessageId(long value) {
        if (value < 0) throw new IllegalArgumentException("id cannot be negative");
        this.value = value;
    }
}
