package com.github.messenger.infrastructure.websocket.message_types;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
public class BasicPayload {
    protected boolean success;

    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected String message;

    protected BasicPayload(boolean success, @NotNull String message) {
        this.success = success;
        this.message = message;
    }

    public BasicPayload(boolean success) {
        this.success = success;
    }
}
