package com.github.messenger.infrastructure.websocket.handlers;

import com.github.messenger.domain.value_objects.UserId;
import com.github.messenger.infrastructure.websocket.infrastructure.OpcodeHandler;
import com.github.messenger.infrastructure.websocket.infrastructure.exceptions.RequestValidationException;
import com.github.messenger.infrastructure.websocket.controllers.request_types.CreatePrivateChatRequest;
import com.github.messenger.infrastructure.websocket.controllers.ChatController;

import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.core.JacksonException;

@Component
public class CreatePrivateChatHandler implements OpcodeHandler {
    private final JsonMapper jsonMapper;
    private final ChatController chatController;

    public CreatePrivateChatHandler(ChatController chatController) {
        this.chatController = chatController;
        this.jsonMapper = new JsonMapper();
    }

    @Override
    public int getOpcode() {
        return 1;
    }

    @Override
    public Object handleOpcode(Object jsonObj, UserId userId) {
        try {
            CreatePrivateChatRequest request = jsonMapper.convertValue(jsonObj, CreatePrivateChatRequest.class);
            return chatController.createPrivateChat(request, userId);
        }
        catch (JacksonException e) {
            throw new RequestValidationException();
        }
    }
}
