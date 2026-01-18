package com.github.messenger.domain.exceptions;

public class PasswordIsWeakException extends RuntimeException {
    public PasswordIsWeakException() {
        super("The password is weak");
    }
}
