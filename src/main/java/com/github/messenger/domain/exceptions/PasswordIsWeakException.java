package com.github.messenger.domain.exceptions;

public class PasswordIsWeakException
        extends DomainException
        implements ClientVisibleException {
    public PasswordIsWeakException() {
        super("The password is weak");
    }
}
