package com.github.messenger.domain.events_system;

public interface EventSubscriber<T extends EventMessage> {
    void execute(T message);
    Class<T> getMessageClass();
}
