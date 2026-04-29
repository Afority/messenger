package com.github.messenger.domain.exceptions;

public class MessageIsTooLong extends DomainException {
    public MessageIsTooLong() {
        super("Message is too long");
    }
}
