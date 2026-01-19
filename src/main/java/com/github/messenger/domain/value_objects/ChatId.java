package com.github.messenger.domain.value_objects;

import lombok.Getter;

public class ChatId {
    @Getter
    private final long value;

    public ChatId(long value) {
        if (value < 0) throw new IllegalArgumentException("chat id cannot be negative");
        this.value = value;
    }
}
