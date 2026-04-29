package com.github.messenger.domain.exceptions;

public class UserAlreadyExistsException extends DomainException {
    public UserAlreadyExistsException() {
        super("User already exists");
    }
}
