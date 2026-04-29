package com.github.messenger.domain.exceptions;

public class UserNameIsInvalidException extends DomainException {
    public UserNameIsInvalidException() {
        super("username is invalid");
    }
}
