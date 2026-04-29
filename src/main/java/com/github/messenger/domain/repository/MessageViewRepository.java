package com.github.messenger.domain.repository;

import com.github.messenger.domain.entity.Message;
import com.github.messenger.domain.usecase.return_types.MessageDto;
import com.github.messenger.domain.value_objects.ChatId;
import com.github.messenger.domain.value_objects.MessageId;
import com.github.messenger.domain.value_objects.MessageNumber;
import com.github.messenger.domain.value_objects.UserId;

import java.util.List;
import java.util.Map;

public interface MessageViewRepository {
    List<UserId> getViewers(MessageNumber number, ChatId chatId);

    List<UserId> getViewers(MessageId messageId);

    Map<MessageId, List<UserId>> getViewers(List<MessageId> messageId);

    void setMark(List<MessageId> messagesIds, UserId userId);

    void setMark(List<MessageNumber> messagesNumbers, ChatId chatId, UserId userId);
}
