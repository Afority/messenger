package com.github.messenger.infrastructure.websocket.infrastructure.exceptions;

/**
 * Ошибка, указывающая на то, запрос не валиден
 */
public class RequestValidationException extends RuntimeException {
    public RequestValidationException(String message) {
        super(message);
    }
    public RequestValidationException() {}
}
