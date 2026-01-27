package com.github.messenger.infrastructure.repository;

import com.github.messenger.domain.value_objects.ChatId;
import com.github.messenger.infrastructure.repository.entity.ChatMemberIdJpaEntity;
import com.github.messenger.infrastructure.repository.entity.ChatMemberJpaEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMembersJpaRepository extends JpaRepository<ChatMemberJpaEntity, ChatMemberIdJpaEntity> {

}
