package com.github.messenger.domain.exceptions;

public class MessageIsEmptyException
        extends DomainException
        implements ClientVisibleException{
    public MessageIsEmptyException() {
        super("message is empty");
    }
}
