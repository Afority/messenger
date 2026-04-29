package com.github.messenger.domain.exceptions;

public class LoginIsInvalidException extends DomainException {
    public LoginIsInvalidException() {
        super("Invalid Login");
    }
}
