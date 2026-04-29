package com.github.messenger.domain.exceptions;

public class AccessDeniedException extends DomainException {
    public AccessDeniedException() {super("access denied");}
    public AccessDeniedException(String message) {super(message);}
}
