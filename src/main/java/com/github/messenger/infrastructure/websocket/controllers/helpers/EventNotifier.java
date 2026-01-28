package com.github.messenger.infrastructure.websocket.controllers.helpers;

import com.github.messenger.domain.entity.TextMessage;
import com.github.messenger.domain.value_objects.ChatId;
import com.github.messenger.domain.value_objects.UserId;
import com.github.messenger.infrastructure.websocket.controllers.server_requests.MessageArrived;
import com.github.messenger.infrastructure.websocket.controllers.server_requests.PrivateChatCreated;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class EventNotifier {
    public static void onPrivateChatCreated(
            SimpMessagingTemplate messagingTemplate,
            UserId recipient,
            UserId creatorId,
            ChatId chatId
    ) {
        messagingTemplate.convertAndSendToUser(
                recipient.toString(),
                "/queue/events",
                new PrivateChatCreated(creatorId.value(), chatId.value().toString())
        );
    }

    public static void onTextMessageArrived(
            SimpMessagingTemplate messagingTemplate,
            UserId recipient,
            TextMessage message
    ) {
        messagingTemplate.convertAndSendToUser(
                recipient.toString(),
                "/queue/events",
                new MessageArrived(
                        message.chat().toString(),
                        message.content().getText(),
                        message.sentAt().getEpochSecond()
                )
        );
    }
}
