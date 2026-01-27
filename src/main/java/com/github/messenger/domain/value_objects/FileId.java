package com.github.messenger.domain.value_objects;

import java.util.Objects;
import java.util.UUID;

public record FileId(UUID id) {
    public FileId {
        Objects.requireNonNull(id);
    }
}
