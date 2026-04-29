package com.github.messenger.infrastructure.repository;

import com.github.messenger.infrastructure.repository.entity.MessageJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageJpaRepository extends JpaRepository<MessageJpaEntity, Long> {
    @Query("""
            SELECT msg
            FROM MessageJpaEntity msg
            WHERE deleted = false and msg.messageNumber >= :messageNumberStart and msg.chat.id = :chatId
            LIMIT :limit
            """)
    List<MessageJpaEntity> getMessagesByNumberAndChatId(UUID chatId, long messageNumberStart, long limit);

    @Query("""
        SELECT msg
        FROM MessageJpaEntity msg
        WHERE msg.deleted = false AND msg.id IN :messageIds
        """)
    List<MessageJpaEntity> getMessagesByIds(List<Long> messageIds);

    @Query("""
        SELECT msg
        FROM MessageJpaEntity msg
        WHERE msg.deleted = false AND msg.messageNumber IN :messagesNumbers AND msg.chat.id = :chatId
        """)
    List<MessageJpaEntity> getMessagesByNumbersAndChatId(List<Long> messagesNumbers, UUID chatId);

    Optional<MessageJpaEntity> findByMessageNumberAndChatId(Long messageNumber, UUID chatId);
    Optional<MessageJpaEntity> findById(Long messageId);

    @Query("""
            SELECT m
            FROM MessageJpaEntity m
            WHERE m.chat.id = :chatId
            ORDER BY m.messageNumber DESC
            LIMIT 1
            """)
    MessageJpaEntity getLastMessage(UUID chatId);
}
