package com.github.messenger.infrastructure.repository;

import com.github.messenger.domain.entity.GroupChat;
import com.github.messenger.domain.value_objects.ChatId;
import com.github.messenger.infrastructure.repository.entity.ChatJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatJpaRepository extends JpaRepository<ChatJpaEntity, UUID> {
    Optional<ChatJpaEntity> findById(ChatId id);
    void save(GroupChat chat);
}
