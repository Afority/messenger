package com.github.messenger.infrastructure.websocket.controllers.response_types;

import com.github.messenger.domain.entity.Attachment;
import com.github.messenger.domain.value_objects.UserId;
import com.github.messenger.infrastructure.websocket.message_types.SuccessPayload;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class MessagesPayload extends SuccessPayload {
    private final long messageNumber;
    private final long senderId;
    private final String chatId;
    private final List<Long> usersViews;
    private final @Nullable String messageText;
    private final @Nullable List<Attachment> attachments;
    private final long sendingTime;

    public MessagesPayload(long messageNumber,
                           long senderId,
                           String chatId,
                           List<Long> usersViews,
                           @Nullable String messageText,
                           @Nullable List<Attachment> attachments,
                           long sendingTime) {
        this.messageNumber = messageNumber;
        this.senderId = senderId;
        this.chatId = chatId;
        this.usersViews = usersViews;
        this.messageText = messageText;
        this.attachments = attachments;
        this.sendingTime = sendingTime;
    }
}
