package com.github.messenger.infrastructure.websocket.handlers;

import com.github.messenger.domain.value_objects.UserId;
import com.github.messenger.infrastructure.websocket.controllers.ChatController;
import com.github.messenger.infrastructure.websocket.infrastructure.OpcodeHandler;
import com.github.messenger.infrastructure.websocket.infrastructure.exceptions.RequestValidationException;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Component
public class GetChatsHandler implements OpcodeHandler {
    ChatController chatController;

    public GetChatsHandler(ChatController chatController) {
        this.chatController = chatController;
    }

    @Override
    public int getOpcode() {
        return 3;
    }

    @Override
    public Object handleOpcode(Object jsonStr, UserId userId) {
        return chatController.getChats(userId);
    }
}
