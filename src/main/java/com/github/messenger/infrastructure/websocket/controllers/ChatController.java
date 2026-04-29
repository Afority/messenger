package com.github.messenger.infrastructure.websocket.controllers;

import com.github.messenger.domain.entity.*;
import com.github.messenger.domain.exceptions.MessageIsEmptyException;
import com.github.messenger.domain.repository.MessageViewRepository;
import com.github.messenger.domain.usecase.*;
import com.github.messenger.domain.usecase.return_types.MessageDto;
import com.github.messenger.domain.value_objects.*;
import com.github.messenger.infrastructure.repository.adapter.dtos.GetChatsResult;
import com.github.messenger.infrastructure.websocket.controllers.request_types.*;
import com.github.messenger.infrastructure.websocket.controllers.response_types.*;
import com.github.messenger.infrastructure.websocket.message_types.SuccessPayload;
import org.springframework.stereotype.Component;
import com.github.messenger.infrastructure.web.dtos.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ChatController {
    private final CreateChatUseCase createChatUseCase;
    private final GetChatsUseCase getChatsUseCase;
    private final GetMessagesUseCase getMessagesUseCase;
    private final SetMessagesViewedUseCase setMessagesViewedUseCase;
    private final MessageViewRepository messageViewRepository;

    SendMessageUseCase sendMessageUseCase;

    ChatController(
        CreateChatUseCase createChatUseCase,
        SendMessageUseCase sendTextMessageUseCase,
        GetChatsUseCase getChatsUseCase,
        GetMessagesUseCase getMessagesUseCase,
        SetMessagesViewedUseCase setMessagesViewedUseCase,
        MessageViewRepository messageViewRepository
    ) {
        this.createChatUseCase = createChatUseCase;
        this.sendMessageUseCase = sendTextMessageUseCase;
        this.getChatsUseCase = getChatsUseCase;
        this.getMessagesUseCase = getMessagesUseCase;
        this.setMessagesViewedUseCase = setMessagesViewedUseCase;
        this.messageViewRepository = messageViewRepository;
    }

    public Object createPrivateChat(CreatePrivateChatRequest request, UserId creatorId) {
        UserId participant = new UserId(request.userId());

        createChatUseCase.createPersonalChat(creatorId, participant, request.userEventId());

        return new SuccessPayload();
    }

    public Object createGroupChat(CreateGroupChatRequest request, UserId creatorId) {
        request.usersIds().add(creatorId.value());

        createChatUseCase.createGroupChat(
            request.usersIds().stream()
                .map(UserId::new)
                .toList(),
            request.name(),
            request.description(),
            request.userEventId()
        );
        return new EventResponse(request.userEventId());
    }

    public Object sendMessage(SendMessageRequest request, UserId senderId) {
        ChatId chatId = new ChatId(UUID.fromString(request.chatId()));

        if (request.messageText() == null && request.attachments() == null) {
            throw new MessageIsEmptyException();
        }

        sendMessageUseCase.execute(
            senderId,
            chatId,
            new TextContent(request.messageText()),
            request.attachments().stream()
                .map(attachment -> new FileId(UUID.fromString(attachment)))
                .toList(),
            request.userEventId()
        );

        return new EventResponse(request.userEventId());
    }

    public Object getChats(UserId userId) {
        List<GetChatsResult> chats = getChatsUseCase.execute(userId);

        List<ChatsDto> result = new ArrayList<>();
        for (GetChatsResult chat : chats) {
            Chat chatEntity = chat.chat();

            String photoId = null;
            String name = null;
            String description = null;

            if (chatEntity instanceof GroupChat groupChat) {
                photoId = groupChat.getPhoto() == null ? null : groupChat.getPhoto().id().toString();
                name = groupChat.getName();
                description = groupChat.getDescription();
            }

            result.add(
                new ChatsDto(
                    chatEntity.getChatId().value().toString(),
                    chatEntity.getType().name(),
                    chat.participants()
                        .stream()
                        .map(user -> new UserDto(
                            user.getId().value(),
                            user.getUsername().getUsernameStr())
                        )
                        .toList(),
                    chatEntity.getLastMessageNumber(),
                    chat.unreadMessages(),
                    chat.lastMessage() == null ? null :
                        new MessagePreviewDto(
                            chat.lastMessage().senderId(),
                            chat.lastMessage().messageText(),
                            chat.lastMessage().sendingTime()
                        ),
                    name,
                    description,
                    photoId
                )
            );
        }
        return result;
    }

    public Object getMessages(GetMessagesRequest request, UserId userId) {
        List<MessagesPayload> result = new ArrayList<>();

        List<MessageDto> messages = getMessagesUseCase.execute(
            userId,
            new ChatId(UUID.fromString(request.chatId())),
            request.start(),
            request.limit()
        );

        for (MessageDto message : messages) {
            List<Long> viewedUsers = message.viewedUsers().stream().map(UserId::value).toList();

            if (message.message() instanceof TextMessage textMessage) {
                TextContent content = textMessage.getContent();
                // todo сделать userviews с timestamp
                result.add(
                    new MessagesPayload(
                        textMessage.getMessageNumber().value(),
                        textMessage.sender().value(),
                        textMessage.chat().value().toString(),
                        viewedUsers,
                        content == null ? null : content.getText(),
                        textMessage.getAttachments(),
                        textMessage.sentAt().getEpochSecond()
                    )
                );
            }
        }
        return result;
    }

    public Object messagesViewed(MessagesViewedRequest request, UserId userId) {
        setMessagesViewedUseCase.executeByMessageNumber(
            request.messagesNumbers().stream()
                .map(MessageNumber::new)
                .toList(),
            new ChatId(UUID.fromString(request.chatId())),
            userId,
            request.userEventId()
        );
        return null;
    }
}
