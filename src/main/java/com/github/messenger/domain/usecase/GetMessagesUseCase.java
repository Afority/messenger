package com.github.messenger.domain.usecase;

import com.github.messenger.domain.entity.Message;
import com.github.messenger.domain.exceptions.ChatNotFoundException;
import com.github.messenger.domain.exceptions.InvalidFilterValueException;
import com.github.messenger.domain.repository.ChatRepository;
import com.github.messenger.domain.repository.MessageRepository;
import com.github.messenger.domain.repository.MessageViewRepository;
import com.github.messenger.domain.usecase.return_types.MessageDto;
import com.github.messenger.domain.value_objects.ChatId;
import com.github.messenger.domain.value_objects.MessageId;
import com.github.messenger.domain.value_objects.UserId;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class GetMessagesUseCase {
    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final MessageViewRepository messageViewRepository;

    public GetMessagesUseCase(MessageRepository messageRepository,
                              ChatRepository chatRepository,
                              MessageViewRepository messageViewRepository) {
        this.messageRepository = messageRepository;
        this.chatRepository = chatRepository;
        this.messageViewRepository = messageViewRepository;
    }

    public List<MessageDto> execute(UserId requestedUserId, ChatId chatId, long messageNumberStart, long limit) {
        if (messageNumberStart < 0 || limit < 0) {
            throw new InvalidFilterValueException("messageNumberStart и limit должны быть положительными");
        }

        if (!chatRepository.userInChat(requestedUserId, chatId)) {
            throw new ChatNotFoundException();
        }

        List<Message> messages = messageRepository.getMessages(chatId, messageNumberStart, limit);
        Map<MessageId, List<UserId>> messagesViews =
            messageViewRepository.getViewers(messages.stream().map(Message::id).toList());

        List<MessageDto> result = new ArrayList<>();

        for (Message message : messages) {
            List<UserId> users = messagesViews.get(message.id());

            result.add(new MessageDto(message, users));
        }

        return result;
    }
}
