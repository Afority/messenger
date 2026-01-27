package com.github.messenger.infrastructure.websocket.controllers;

import com.github.messenger.domain.exceptions.ClientVisibleException;
import com.github.messenger.infrastructure.websocket.controllers.helpers.RequestHelper;
import com.github.messenger.infrastructure.websocket.controllers.helpers.ResponseHelper;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class ExceptionHandlerController {
    @MessageExceptionHandler
    public void handleException(
            Exception e,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        try {
            long requestId = RequestHelper.extractRequestId(headerAccessor);
        }
        catch (IllegalArgumentException e) {
            ResponseHelper.sendError(requestId, e.getMessage());
        }

        if (e instanceof ClientVisibleException) {
            ResponseHelper.sendError(requestId, e.getMessage());
        } else {
            log.error("Internal error", e);

            ResponseHelper.sendError(
                    requestId,
                    0,
                    "Internal server error"
            );
        }
    }

}
