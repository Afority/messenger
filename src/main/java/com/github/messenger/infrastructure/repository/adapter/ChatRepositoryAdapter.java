package com.github.messenger.infrastructure.repository.adapter;

import com.github.messenger.domain.entity.GroupChat;
import com.github.messenger.domain.entity.PersonalChat;
import com.github.messenger.domain.repository.ChatRepository;
import com.github.messenger.domain.value_objects.ChatId;
import com.github.messenger.domain.value_objects.ChatType;
import com.github.messenger.domain.value_objects.UserId;
import com.github.messenger.infrastructure.repository.ChatMembersJpaRepository;
import com.github.messenger.infrastructure.repository.entity.ChatJpaEntity;
import com.github.messenger.infrastructure.repository.entity.ChatMemberJpaEntity;
import com.github.messenger.infrastructure.repository.entity.UserJpaEntity;
import com.github.messenger.infrastructure.repository.mapper.ChatMapper;
import com.github.messenger.infrastructure.repository.ChatJpaRepository;
import com.github.messenger.infrastructure.repository.persistence_model.ChatPersistenceModel;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ChatRepositoryAdapter implements ChatRepository {
    ChatJpaRepository chatJpaRepository;
    ChatMembersJpaRepository chatMembersJpaRepository;
    ChatMapper chatMapper;

    public ChatRepositoryAdapter(ChatJpaRepository chatJpaRepository,
                                 ChatMembersJpaRepository chatMembersJpaRepository,
                                 ChatMapper chatMapper) {
        this.chatJpaRepository = chatJpaRepository;
        this.chatMembersJpaRepository = chatMembersJpaRepository;
        this.chatMapper = chatMapper;
    }

    @Override
    @Transactional
    public ChatId savePersonalChat(List<UserId> members) {
        ChatJpaEntity savedChat = chatJpaRepository.save(new ChatJpaEntity(null, null, null, ChatType.PERSONAL, null));

        chatMembersJpaRepository.saveAll(
                members.stream().map(memberId ->
                                new ChatMemberJpaEntity(new UserJpaEntity(memberId.value()), savedChat)).toList()
        );
        return new ChatId(savedChat.getId());
    }
}
