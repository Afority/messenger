package com.github.messenger.domain.events_system;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class EventBroker {
    ConcurrentMap<Class<?>, Set<EventSubscriber<?>>> subscribers;

    public EventBroker(List<EventSubscriber<?>> eventsList) {
        this.subscribers = new ConcurrentHashMap<>();

        for (EventSubscriber<?> event : eventsList) {
            subscribe(event.getMessageClass(), event);
        }
    }

    public <T extends EventMessage> void publishEvent (T message) {
        Set<EventSubscriber<?>> subscribersSet = subscribers.get(message.getClass());

        if (subscribersSet != null) {
            for (EventSubscriber<?> subscriber : subscribersSet) {
                EventSubscriber<T> eventSubscriber = (EventSubscriber<T>) subscriber;
                eventSubscriber.execute(message);
            }
        }
    }

    private void subscribe(Class<?> eventClass, EventSubscriber<?> event) {
        subscribers
                .computeIfAbsent(eventClass, k -> Collections.synchronizedSet(new HashSet<>()))
                .add(event);
    }

}
