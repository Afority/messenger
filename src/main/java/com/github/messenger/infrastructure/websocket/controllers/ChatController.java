package com.github.messenger.infrastructure.websocket.controllers;

import com.github.messenger.domain.usecase.CreateChatUseCase;
import com.github.messenger.domain.value_objects.ChatId;
import com.github.messenger.domain.value_objects.UserId;
import com.github.messenger.infrastructure.websocket.controllers.helpers.RequestHelper;
import com.github.messenger.infrastructure.websocket.controllers.helpers.ResponseHelper;
import com.github.messenger.infrastructure.websocket.controllers.request_types.CreatePrivateChatRequest;
import com.github.messenger.infrastructure.websocket.controllers.response_types.CreateChatResponse;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
public class ChatController {
    CreateChatUseCase createChatUseCase;
    SimpMessagingTemplate messagingTemplate;

    ChatController(CreateChatUseCase createChatUseCase,
                   SimpMessagingTemplate messagingTemplate) {
        this.createChatUseCase = createChatUseCase;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/private-chat.create")
    public void createPrivateChat(
            CreatePrivateChatRequest request,
            SimpMessageHeaderAccessor headerAccessor
    )
    {
        try{
            RequestHelper.validate(request);

            UserId userId = RequestHelper.extractUserId(headerAccessor);
            ChatId chatId = createChatUseCase.createPersonalChat(List.of(userId, new UserId(request.userId())));
            ResponseHelper.sendSuccess(messagingTemplate, request.requestId(),  chatId);
        }
        catch (Exception e) {
            ResponseHelper.sendError(messagingTemplate, request.requestId(), e.getMessage());
        }
    }
}
