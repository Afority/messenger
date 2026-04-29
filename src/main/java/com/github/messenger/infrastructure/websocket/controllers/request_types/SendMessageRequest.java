package com.github.messenger.infrastructure.websocket.controllers.request_types;

import com.github.messenger.domain.value_objects.ForwardType;

import java.util.List;

//record Forward(
//    ForwardType type,
//    String chatId,
//    long messageId
//) { }

public record SendMessageRequest(
        String chatId,
        String messageText,
        List<String> attachments,
        int userEventId
) { }
