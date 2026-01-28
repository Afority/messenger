package com.github.messenger.infrastructure.websocket.controllers;

import com.github.messenger.domain.entity.TextMessage;
import com.github.messenger.domain.usecase.CreateChatUseCase;
import com.github.messenger.domain.usecase.GetDirectChatOtherParticipantUseCase;
import com.github.messenger.domain.usecase.SendPrivateTextMessageUseCase;
import com.github.messenger.domain.value_objects.ChatId;
import com.github.messenger.domain.value_objects.TextContent;
import com.github.messenger.domain.value_objects.UserId;
import com.github.messenger.infrastructure.websocket.controllers.helpers.EventNotifier;
import com.github.messenger.infrastructure.websocket.controllers.helpers.RequestHelper;
import com.github.messenger.infrastructure.websocket.controllers.helpers.ResponseHelper;
import com.github.messenger.infrastructure.websocket.controllers.request_types.CreatePrivateChatRequest;
import com.github.messenger.infrastructure.websocket.controllers.request_types.SendTextMessageRequest;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
public class ChatController {
    CreateChatUseCase createChatUseCase;
    SimpMessagingTemplate messagingTemplate;
    GetDirectChatOtherParticipantUseCase getDirectChatOtherParticipantUseCase;

    SendPrivateTextMessageUseCase sendPrivateTextMessageUseCase;

    ChatController(CreateChatUseCase createChatUseCase,
                   SimpMessagingTemplate messagingTemplate,
                   GetDirectChatOtherParticipantUseCase getDirectChatOtherParticipantUseCase
    ) {
        this.createChatUseCase = createChatUseCase;
        this.messagingTemplate = messagingTemplate;
        this.getDirectChatOtherParticipantUseCase = getDirectChatOtherParticipantUseCase;
    }

    @MessageMapping("/private-chat.create")
    public void createPrivateChat(
            CreatePrivateChatRequest request,
            SimpMessageHeaderAccessor headerAccessor
    )
    {
        RequestHelper.validate(request);
        UserId creatorId = RequestHelper.extractUserId(headerAccessor);
        UserId companionId = new UserId(request.userId());

        List<UserId> members = List.of(creatorId, companionId);

        ChatId chatId = createChatUseCase.createPersonalChat(members);

        ResponseHelper.sendChatCreated(messagingTemplate, creatorId, request.requestId(),  chatId);
        EventNotifier.onPrivateChatCreated(messagingTemplate, companionId, creatorId, chatId);
    }

    @MessageMapping("/private-chat.send_private_message")
    public void sendMessageToPrivateChat(
            SendTextMessageRequest request,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        RequestHelper.validate(request);
        UserId senderId = RequestHelper.extractUserId(headerAccessor);
        ChatId chatId = new ChatId(UUID.fromString(request.chatId()));

        TextMessage msg = sendPrivateTextMessageUseCase.send(
                senderId,
                chatId,
                new TextContent(request.message())
        );

        ResponseHelper.sendSimpleResponse(
                messagingTemplate,
                senderId,
                request.requestId(),
                false,
                null
        );

        UserId receiverId = getDirectChatOtherParticipantUseCase.execute(chatId, senderId);

        EventNotifier.onTextMessageArrived(
                messagingTemplate,
                receiverId,
                msg
        );
    }
}
