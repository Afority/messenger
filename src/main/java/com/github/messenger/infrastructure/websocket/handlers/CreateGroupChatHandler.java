package com.github.messenger.infrastructure.websocket.handlers;

import com.github.messenger.domain.value_objects.UserId;
import com.github.messenger.infrastructure.websocket.controllers.ChatController;
import com.github.messenger.infrastructure.websocket.controllers.request_types.CreateGroupChatRequest;
import com.github.messenger.infrastructure.websocket.infrastructure.OpcodeHandler;
import com.github.messenger.infrastructure.websocket.infrastructure.exceptions.RequestValidationException;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Component
public class CreateGroupChatHandler implements OpcodeHandler {
    private final JsonMapper jsonMapper;
    private final ChatController chatController;

    public CreateGroupChatHandler(ChatController chatController) {
        this.jsonMapper = new JsonMapper();
        this.chatController = chatController;
    }

    @Override
    public int getOpcode() {
        return 2;
    }

    @Override
    public @Nullable Object handleOpcode(Object jsonObj, UserId userId) {
        try {
            CreateGroupChatRequest request = jsonMapper.convertValue(jsonObj, CreateGroupChatRequest.class);
            return chatController.createGroupChat(request, userId);
        } catch (JacksonException e) {
            throw new RequestValidationException();
        }
    }
}
