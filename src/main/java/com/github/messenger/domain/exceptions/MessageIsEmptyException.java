package com.github.messenger.domain.exceptions;

public class MessageIsEmptyException extends DomainException {
    public MessageIsEmptyException() {
        super("message is empty");
    }
}
