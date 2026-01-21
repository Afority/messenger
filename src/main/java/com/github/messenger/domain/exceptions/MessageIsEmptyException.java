package com.github.messenger.domain.exceptions;

public class MessageIsEmptyException extends RuntimeException {
    public MessageIsEmptyException() {
        super("message is empty");
    }
}
