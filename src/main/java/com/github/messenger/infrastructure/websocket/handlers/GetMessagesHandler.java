package com.github.messenger.infrastructure.websocket.handlers;

import com.github.messenger.domain.value_objects.UserId;
import com.github.messenger.infrastructure.websocket.controllers.ChatController;
import com.github.messenger.infrastructure.websocket.infrastructure.OpcodeHandler;
import com.github.messenger.infrastructure.websocket.infrastructure.UserCommunication;
import com.github.messenger.infrastructure.websocket.infrastructure.UsersCommunication;
import com.github.messenger.infrastructure.websocket.infrastructure.exceptions.RequestValidationException;
import com.github.messenger.infrastructure.websocket.controllers.request_types.GetMessagesRequest;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.util.Objects;

@Component
public class GetMessagesHandler implements OpcodeHandler {
    JsonMapper jsonMapper;
    ChatController chatController;

    public GetMessagesHandler(ChatController chatController) {
        this.chatController = chatController;
        this.jsonMapper = new JsonMapper();
    }

    @Override
    public int getOpcode() {
        return 5;
    }

    @Override
    public Object handleOpcode(Object jsonObj, UserId userId) {
        GetMessagesRequest request = null;
        try {
            request = jsonMapper.convertValue(jsonObj, GetMessagesRequest.class);
            Objects.requireNonNull(request.chatId());
        }
        catch (JacksonException | NullPointerException e) {
            throw new RequestValidationException();
        }
        return chatController.getMessages(request, userId);
    }
}
