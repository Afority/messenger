package com.github.messenger.infrastructure.websocket.controllers.response_types;

import com.github.messenger.infrastructure.web.dtos.UserDto;
import com.github.messenger.infrastructure.websocket.message_types.SuccessPayload;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class UsersPayload extends SuccessPayload {
    private List<UserDto> users;

    public UsersPayload(List<UserDto> users) {
        this.users = users;
    }
}
