package com.github.messenger.domain.exceptions;

public class ChatNotFoundException extends DomainException {
    public ChatNotFoundException(String message) {
        super(message);
    }
    public ChatNotFoundException(){
        super("Chat not found");
    }
}
