package com.github.messenger.domain.exceptions;

public class LoginIsInvalidException
        extends DomainException
        implements ClientVisibleException {
    public LoginIsInvalidException() {
        super("Invalid Login");
    }
}
