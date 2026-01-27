package com.github.messenger.infrastructure.repository.mapper;

import com.github.messenger.domain.entity.GroupChat;
import com.github.messenger.domain.entity.PersonalChat;
import com.github.messenger.domain.value_objects.ChatId;
import com.github.messenger.domain.value_objects.ChatType;
import com.github.messenger.domain.value_objects.FileId;
import com.github.messenger.domain.value_objects.UserId;
import com.github.messenger.infrastructure.repository.entity.ChatJpaEntity;
import com.github.messenger.infrastructure.repository.entity.ChatMemberJpaEntity;
import com.github.messenger.infrastructure.repository.entity.FileJpaEntity;
import com.github.messenger.infrastructure.repository.entity.UserJpaEntity;
import com.github.messenger.infrastructure.repository.persistence_model.ChatPersistenceModel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChatMapper {
    public ChatPersistenceModel toGroupChatPersistenceModel(GroupChat chat) {
        ChatJpaEntity chatJpaEntity = new ChatJpaEntity(chat.getChatId().value());

        List<ChatMemberJpaEntity> members = chat.getParticipants()
                .stream()
                .map(userId ->
                        new ChatMemberJpaEntity(new UserJpaEntity(userId.value()), chatJpaEntity))
                .toList();

        FileJpaEntity file = chat.getPhoto() != null ? new FileJpaEntity(chat.getPhoto().id()) : null;

        return new ChatPersistenceModel(
                new ChatJpaEntity(
                    chat.getChatId().value(),
                    chat.getName(),
                    chat.getDescription(),
                    ChatType.GROUP,
                    file
                ),
                members
        );
    }

    public ChatPersistenceModel toPersonalChatPersistenceModel(GroupChat chat) {
        List<ChatMemberJpaEntity> members = chat.getParticipants()
                .stream()
                .map(userId ->
                        new ChatMemberJpaEntity(new UserJpaEntity(userId.value()), new ChatJpaEntity(chat.getChatId().value())))
                .toList();

        return new ChatPersistenceModel(
                new ChatJpaEntity(
                        chat.getChatId().value()
                ),
                members
        );

    }

    public PersonalChat toPersonalChatDomainEntity(ChatJpaEntity jpaEntity) {
        List<UserId> members = jpaEntity.getMembers()
                .stream()
                .map(entity -> new UserId(entity.getUser().getId()))
                .toList();

        return new PersonalChat(new ChatId(jpaEntity.getId()), members);
    }

    public GroupChat toGroupChatDomainEntity(ChatJpaEntity jpaEntity) {
        List<UserId> members = jpaEntity.getMembers()
                .stream()
                .map(entity -> new UserId(entity.getUser().getId()))
                .toList();

        return new GroupChat(
                new ChatId(jpaEntity.getId()),
                jpaEntity.getName(),
                jpaEntity.getDescription(),
                new FileId(jpaEntity.getPhoto().getId()),
                members
        );
    }
}
