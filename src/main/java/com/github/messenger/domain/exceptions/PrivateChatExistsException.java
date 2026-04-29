package com.github.messenger.domain.exceptions;

public class PrivateChatExistsException extends DomainException {
    public PrivateChatExistsException(String message) {
        super(message);
    }
}
