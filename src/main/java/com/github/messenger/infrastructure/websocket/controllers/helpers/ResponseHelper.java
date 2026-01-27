package com.github.messenger.infrastructure.websocket.controllers.helpers;

import com.github.messenger.domain.value_objects.ChatId;
import com.github.messenger.infrastructure.websocket.controllers.response_types.CreateChatResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;


public class ResponseHelper {
    public static void sendError(SimpMessagingTemplate messagingTemplate, long requestId, String message) {
        messagingTemplate.convertAndSend(
                "/topic/response",
                new CreateChatResponse(null, requestId, true, message)
        );
    }

    public static void sendSuccess(SimpMessagingTemplate messagingTemplate, long requestId, ChatId chatId) {
        messagingTemplate.convertAndSend(
                "/topic/response",
                new CreateChatResponse(chatId.value().toString(), requestId, false, null)
        );
    }
}
