package com.github.messenger.infrastructure.websocket.controllers.request_types;

import java.util.List;

public record CreateGroupChatRequest(
        List<Long> usersIds,
        String name,
        String description,
        String photoId,
        int userEventId
) {
}
