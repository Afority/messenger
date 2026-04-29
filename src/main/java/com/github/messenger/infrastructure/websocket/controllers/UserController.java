package com.github.messenger.infrastructure.websocket.controllers;

import com.github.messenger.domain.usecase.GetUserByLoginUseCase;
import com.github.messenger.domain.usecase.GetUsersByLikeUsernameUseCase;
import com.github.messenger.domain.value_objects.Username;
import com.github.messenger.infrastructure.websocket.controllers.request_types.GetUserByUsernameRequest;
import com.github.messenger.infrastructure.web.dtos.UserDto;
import com.github.messenger.infrastructure.websocket.controllers.response_types.UsersPayload;
import org.springframework.stereotype.Component;

@Component
public class UserController {
    private final GetUserByLoginUseCase getUserIdUseCase;
    private final GetUsersByLikeUsernameUseCase getUsersByLikeUsernameUseCase;

    public UserController(GetUsersByLikeUsernameUseCase getUsersByLikeUsernameUseCase,
                          GetUserByLoginUseCase getUserIdUseCase) {
        this.getUsersByLikeUsernameUseCase = getUsersByLikeUsernameUseCase;
        this.getUserIdUseCase = getUserIdUseCase;
    }

    public Object getUser(GetUserByUsernameRequest request) {
        return new UsersPayload(
                getUsersByLikeUsernameUseCase.execute(new Username(request.username())).stream()
                        .map(user -> new UserDto(user.getId().value(), user.getUsername().getUsernameStr()))
                        .toList()
        );
    }
}
