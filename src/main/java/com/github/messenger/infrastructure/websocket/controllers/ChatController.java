package com.github.messenger.infrastructure.websocket.controllers;

import com.github.messenger.domain.usecase.CreateChatUseCase;
import com.github.messenger.domain.usecase.GetDirectChatOtherParticipantUseCase;
import com.github.messenger.domain.usecase.SendPrivateTextMessageUseCase;
import com.github.messenger.infrastructure.websocket.request_types.CreatePrivateChatRequest;
import com.github.messenger.infrastructure.websocket.request_types.SendTextMessageRequest;


public class ChatController {
    CreateChatUseCase createChatUseCase;
    GetDirectChatOtherParticipantUseCase getDirectChatOtherParticipantUseCase;

    SendPrivateTextMessageUseCase sendPrivateTextMessageUseCase;

    ChatController(CreateChatUseCase createChatUseCase,
                   GetDirectChatOtherParticipantUseCase getDirectChatOtherParticipantUseCase
    ) {
        this.createChatUseCase = createChatUseCase;
        this.getDirectChatOtherParticipantUseCase = getDirectChatOtherParticipantUseCase;
    }

    public void createPrivateChat(CreatePrivateChatRequest request)
    {
//        RequestHelper.validate(request);
//        UserId creatorId = RequestHelper.extractUserId(headerAccessor);
//        UserId companionId = new UserId(request.userId());
//
//        List<UserId> members = List.of(creatorId, companionId);
//
//        ChatId chatId = createChatUseCase.createPersonalChat(members);

//        ResponseHelper.sendChatCreated(messagingTemplate, creatorId, request.requestId(),  chatId);
//        EventNotifier.onPrivateChatCreated(messagingTemplate, companionId, creatorId, chatId);
    }

    public void sendMessageToPrivateChat(SendTextMessageRequest request) {
//        RequestHelper.validate(request);
//        UserId senderId = RequestHelper.extractUserId(headerAccessor);
//        ChatId chatId = new ChatId(UUID.fromString(request.chatId()));
//
//        TextMessage msg = sendPrivateTextMessageUseCase.send(
//                senderId,
//                chatId,
//                new TextContent(request.message())
//        );

//        ResponseHelper.sendSimpleResponse(
//                messagingTemplate,
//                senderId,
//                request.requestId(),
//                false,
//                null
//        );

//        UserId receiverId = getDirectChatOtherParticipantUseCase.execute(chatId, senderId);

//        EventNotifier.onTextMessageArrived(
//                messagingTemplate,
//                receiverId,
//                msg
//        );
    }
}
