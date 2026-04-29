package com.github.messenger.infrastructure.websocket.handlers;


import com.github.messenger.domain.value_objects.UserId;
import com.github.messenger.infrastructure.websocket.controllers.ChatController;
import com.github.messenger.infrastructure.websocket.infrastructure.OpcodeHandler;
import com.github.messenger.infrastructure.websocket.infrastructure.exceptions.RequestValidationException;
import com.github.messenger.infrastructure.websocket.controllers.request_types.SendMessageRequest;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Component
public class SendMessageHandler implements OpcodeHandler {
    ChatController chatController;
    JsonMapper jsonMapper;

    public SendMessageHandler(ChatController chatController) {
        this.jsonMapper = new JsonMapper();
        this.chatController = chatController;
    }

    @Override
    public int getOpcode() {
        return 4;
    }

    @Override
    public Object handleOpcode(Object jsonObj, UserId userId) {
        SendMessageRequest request;
        try {
            request = jsonMapper.convertValue(jsonObj, SendMessageRequest.class);
        }
        catch (JacksonException ignored) {
            throw new RequestValidationException();
        }

        if (request.chatId() == null || request.messageText() == null) {
            throw new RequestValidationException();
        }

        return chatController.sendMessage(request, userId);
    }
}
