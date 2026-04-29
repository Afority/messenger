package com.github.messenger.infrastructure.websocket.data_storage;

import com.github.messenger.domain.usecase.GetChatsUseCase;
import com.github.messenger.domain.value_objects.ChatId;
import com.github.messenger.domain.value_objects.UserId;
import com.github.messenger.infrastructure.repository.adapter.dtos.GetChatsResult;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class ChatMembersData {
    private final ConcurrentMap<ChatId, Set<UserId>> subscribersByChat;
    private final ConcurrentMap<UserId, Set<ChatId>> chatsBySubscriber;
    private final GetChatsUseCase getChatsUseCase;

    public ChatMembersData(GetChatsUseCase getChatsUseCase) {
        this.getChatsUseCase = getChatsUseCase;
        this.subscribersByChat = new ConcurrentHashMap<>();
        this.chatsBySubscriber = new ConcurrentHashMap<>();
    }

    // Регистрация пользователя под все чаты, в которых он состоит
    public void subscribeUserToAllChat(UserId userId) {
        List<GetChatsResult> chats = getChatsUseCase.execute(userId);

        for (GetChatsResult chat : chats) {
            subscribeToChat(chat.chat().getChatId(), userId);
        }
    }

    public void unsubscribeUser(UserId userId) {
        for (ChatId chatId : chatsBySubscriber.get(userId)) {
            subscribersByChat.get(chatId).remove(userId);
        }
        chatsBySubscriber.remove(userId);
    }

    public void subscribeToChat(ChatId chat, UserId userId) {
        // заполнение chatBySubscriber
        if (chatsBySubscriber.get(userId) == null) {
            Set<ChatId> chats = Collections.synchronizedSet(new HashSet<>());
            chats.add(chat);
            chatsBySubscriber.put(userId, chats);
        }
        else {
            chatsBySubscriber.get(userId).add(chat);
        }

        // заполнение subscribersByChat
        if (subscribersByChat.containsKey(chat)) {
            subscribersByChat.get(chat).add(userId);
        } else {
            Set<UserId> userIds = Collections.synchronizedSet(new HashSet<>());
            userIds.add(userId);
            subscribersByChat.put(chat, userIds);
        }
    }

    public Set<UserId> getChatParticipants(ChatId chatId) {
        return subscribersByChat.get(chatId);
    }
}
