package com.github.messenger.infrastructure.websocket.controllers.helpers;

import com.github.messenger.domain.value_objects.UserId;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RequestHelper {
    /*
     * Проверка на корректность запроса.
     */
    public static void validate(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("Object is null");
        }

        // проверка на поле requestId
        Arrays.stream(object.getClass().getDeclaredFields())
                .filter(field -> field.getName().equals("requestId"))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("missing requestId"));

        // проверка на нулевые поля
        List<String> missingFields = new ArrayList<>();
        for (Field field : object.getClass().getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)) {
                continue;
            }

            field.setAccessible(true);
            try {
                if (field.get(object) == null) {
                    missingFields.add(field.getName());
                }
            } catch (IllegalAccessException ignored) {}
        }
        if (!missingFields.isEmpty()) {
            throw new IllegalArgumentException("Missing fields: " + Arrays.toString(missingFields.toArray()));
        }
    }

    public static UserId extractUserId(SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> attrs = headerAccessor.getSessionAttributes();

        if (attrs == null || !attrs.containsKey("userId")) {
            throw new IllegalArgumentException("User not authenticated");
        }

        return new UserId((Long) attrs.get("userId"));
    }

    public static long extractRequestId(SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> attrs = headerAccessor.getSessionAttributes();

        if (attrs == null || !attrs.containsKey("requestId")) {
            throw new IllegalArgumentException("request id is null");
        }

        return (Long) attrs.get("requestId");
    }
}
