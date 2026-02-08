package com.github.messenger.infrastructure.repository;

import com.github.messenger.infrastructure.repository.entity.ChatMemberIdJpaEntity;
import com.github.messenger.infrastructure.repository.entity.ChatMemberJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatMembersJpaRepository extends JpaRepository<ChatMemberJpaEntity, ChatMemberIdJpaEntity> {
    @Query("""
        SELECT chatMember FROM ChatMemberJpaEntity chatMember
        WHERE chatMember.chat.id = :chatId
    """)
    List<ChatMemberJpaEntity> getChatMembers(@Param("chatId") UUID chatId);
}
