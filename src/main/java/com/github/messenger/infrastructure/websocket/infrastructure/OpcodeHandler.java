package com.github.messenger.infrastructure.websocket.infrastructure;

import com.github.messenger.domain.value_objects.UserId;
import org.jetbrains.annotations.Nullable;

public interface OpcodeHandler {
    int getOpcode();

    @Nullable Object handleOpcode(Object jsonObj, UserId userId);
}
