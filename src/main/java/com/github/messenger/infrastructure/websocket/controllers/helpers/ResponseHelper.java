package com.github.messenger.infrastructure.websocket.controllers.helpers;

import com.github.messenger.domain.value_objects.ChatId;
import com.github.messenger.domain.value_objects.UserId;
import com.github.messenger.infrastructure.websocket.controllers.response_types.ChatCreatedResponse;
import com.github.messenger.infrastructure.websocket.controllers.response_types.InternalErrorResponse;
import com.github.messenger.infrastructure.websocket.controllers.response_types.SimpleResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;


public class ResponseHelper {
    public static void sendError(
            SimpMessagingTemplate messagingTemplate,
            UserId userId,
            long requestId,
            String message
    ) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/errors",
                new InternalErrorResponse(requestId, true, message)
        );
    }

    public static void sendChatCreated(
            SimpMessagingTemplate messagingTemplate,
            UserId userId,
            long requestId,
            ChatId chatId
    ) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/events",
                new ChatCreatedResponse(
                        "response",
                        chatId.value().toString(),
                        requestId,
                        false,
                        null
                )
        );
    }

    public static void sendSimpleResponse(
            SimpMessagingTemplate messagingTemplate,
            UserId userId,
            long requestId,
            boolean isError,
            String errorMessage
    ) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/events",
                new SimpleResponse(
                        "response",
                        requestId,
                        isError,
                        errorMessage
                )
        );
    }
}