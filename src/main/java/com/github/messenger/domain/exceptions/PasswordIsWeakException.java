package com.github.messenger.domain.exceptions;

public class PasswordIsWeakException extends DomainException {
    public PasswordIsWeakException() {
        super("The password is weak");
    }
}
