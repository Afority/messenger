package com.github.messenger.domain.exceptions;

public class MessageIsTooLong
        extends DomainException
        implements ClientVisibleException {
    public MessageIsTooLong() {
        super("Message is too long");
    }
}
