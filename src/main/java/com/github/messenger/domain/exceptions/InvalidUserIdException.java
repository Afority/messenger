package com.github.messenger.domain.exceptions;

public class InvalidUserIdException extends RuntimeException {
    public InvalidUserIdException() {
        super("Invalid userId");
    }
}
