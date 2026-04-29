package com.github.messenger.domain.value_objects;

import java.util.Objects;
import java.util.UUID;

public record ChatId (UUID value) {
    public ChatId {
        Objects.requireNonNull(value, "id cannot be null");
    }
}
