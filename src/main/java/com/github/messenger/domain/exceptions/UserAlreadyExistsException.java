package com.github.messenger.domain.exceptions;

public class UserAlreadyExistsException
        extends DomainException
        implements ClientVisibleException {
    public UserAlreadyExistsException() {
        super("User already exists");
    }
}
