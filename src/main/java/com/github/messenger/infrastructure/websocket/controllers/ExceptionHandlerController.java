package com.github.messenger.infrastructure.websocket.controllers;

import com.github.messenger.domain.exceptions.ClientVisibleException;
import com.github.messenger.domain.value_objects.UserId;
import com.github.messenger.infrastructure.websocket.controllers.helpers.RequestHelper;
import com.github.messenger.infrastructure.websocket.controllers.helpers.ResponseHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ExceptionHandlerController {

    private static final Logger log = LoggerFactory.getLogger(ExceptionHandlerController.class);

    private final SimpMessagingTemplate messagingTemplate;

    public ExceptionHandlerController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageExceptionHandler
    public void handleException(
            Exception e,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        long requestId;
        try {
            requestId = RequestHelper.extractRequestId(headerAccessor);
        } catch (IllegalArgumentException ex) {
            // нет requestId — нечего отправлять на клиент
            log.warn("Missing requestId in WS headers", ex);
            return;
        }

        UserId userId;
        try {
            userId = RequestHelper.extractUserId(headerAccessor);
        } catch (IllegalArgumentException ex) {
            log.warn("Missing userId in WS headers", ex);
            return;
        }

        if (e instanceof ClientVisibleException) {
            ResponseHelper.sendError(messagingTemplate, userId, requestId, e.getMessage());
        } else {
            log.error("Internal error", e);
            ResponseHelper.sendError(
                    messagingTemplate,
                    userId,
                    requestId,
                    "Internal server error"
            );
        }
    }
}
