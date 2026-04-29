package com.github.messenger.domain.exceptions;

public class MessageNotFoundException extends DomainException {
    public MessageNotFoundException() {
        super("Message not found");
    }
}
