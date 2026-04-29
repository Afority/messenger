package com.github.messenger.infrastructure.websocket.controllers.response_types;

import com.github.messenger.infrastructure.websocket.message_types.SuccessPayload;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LoginPayload extends SuccessPayload {
    private final String login;

    public LoginPayload(String login) {
        this.login = login;
    }
}
