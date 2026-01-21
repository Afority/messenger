package com.github.messenger.domain.value_objects;

import java.util.Objects;
import java.util.UUID;

public class FileContent {
    private UUID id;
    private boolean isDone;

    public FileContent(UUID id, boolean isDone) {
        Objects.requireNonNull(id);

        this.id = id;
        this.isDone = isDone;
    }
}
