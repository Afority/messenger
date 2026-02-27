package com.github.messenger.infrastructure.websocket.validators;

public class ValidateError extends RuntimeException {
    public ValidateError(String message) {
        super(message);
    }
}
