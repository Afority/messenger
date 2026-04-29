package com.github.messenger.infrastructure.websocket.handlers;

import com.github.messenger.domain.value_objects.UserId;
import com.github.messenger.infrastructure.websocket.controllers.UserController;
import com.github.messenger.infrastructure.websocket.controllers.request_types.GetUserByUsernameRequest;
import com.github.messenger.infrastructure.websocket.infrastructure.OpcodeHandler;
import com.github.messenger.infrastructure.websocket.infrastructure.exceptions.RequestValidationException;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Component
public class GetUserByUsernameHandler implements OpcodeHandler {
    private final UserController userController;
    JsonMapper jsonMapper;

    public GetUserByUsernameHandler(UserController userController) {
        this.userController = userController;
        this.jsonMapper = new JsonMapper();
    }

    @Override
    public int getOpcode() {
        return 6;
    }

    @Override
    public @Nullable Object handleOpcode(Object jsonObj, UserId userId) {
        try {
            GetUserByUsernameRequest request = jsonMapper.convertValue(jsonObj, GetUserByUsernameRequest.class);
            return userController.getUser(request);
        }
        catch (JacksonException e) {
            throw new RequestValidationException();
        }
    }
}
