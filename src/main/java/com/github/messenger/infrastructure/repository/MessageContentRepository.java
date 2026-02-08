package com.github.messenger.infrastructure.repository;

import com.github.messenger.infrastructure.repository.entity.MessageContentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageContentRepository extends JpaRepository<MessageContentJpaEntity, Long> {
}
