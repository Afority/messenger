package com.github.messenger.infrastructure.websocket.mappers;

import com.github.messenger.domain.entity.User;
import com.github.messenger.infrastructure.web.dtos.UserDto;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(user.getId().value(), user.getUsername().getUsernameStr());
    }
}
