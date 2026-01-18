package com.github.messenger.domain.exceptions;

public class LoginIsInvalidException extends RuntimeException {
    public LoginIsInvalidException() {
        super("Invalid Login");
    }
}
