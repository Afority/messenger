package com.github.messenger.domain.exceptions;

public class MessageIsTooLong extends RuntimeException {
    public MessageIsTooLong() {
        super("Message is too long");
    }
}
