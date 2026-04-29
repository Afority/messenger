package com.github.messenger.infrastructure.websocket.handlers;

import com.github.messenger.domain.value_objects.UserId;
import com.github.messenger.infrastructure.websocket.controllers.ChatController;
import com.github.messenger.infrastructure.websocket.controllers.request_types.MessagesViewedRequest;
import com.github.messenger.infrastructure.websocket.infrastructure.OpcodeHandler;
import com.github.messenger.infrastructure.websocket.infrastructure.exceptions.RequestValidationException;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Component
public class MessagesViewedHandler implements OpcodeHandler {
    private final JsonMapper jsonMapper;
    private final ChatController chatController;

    public MessagesViewedHandler(ChatController chatController) {
        this.jsonMapper = new JsonMapper();
        this.chatController = chatController;
    }

    @Override
    public int getOpcode() {
        return 7;
    }

    @Override
    public @Nullable Object handleOpcode(Object jsonObj, UserId userId) {
        try {
            MessagesViewedRequest request = jsonMapper.convertValue(jsonObj, MessagesViewedRequest.class);
            return chatController.messagesViewed(request, userId);
        }
        catch (JacksonException ignored) {
            throw new RequestValidationException();
        }
    }
}
