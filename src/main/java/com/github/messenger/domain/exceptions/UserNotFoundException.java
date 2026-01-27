package com.github.messenger.domain.exceptions;

import com.github.messenger.domain.value_objects.UserId;

public class UserNotFoundException
        extends DomainException
        implements ClientVisibleException {
    public UserNotFoundException(UserId userId) {
        super(String.format("Пользователь %d не найден", userId.value()));
    }
}
