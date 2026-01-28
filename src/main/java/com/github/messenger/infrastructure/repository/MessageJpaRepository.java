package com.github.messenger.infrastructure.repository;

import com.github.messenger.infrastructure.repository.entity.MessageJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageJpaRepository extends JpaRepository<MessageJpaEntity, Long> {
    @Query("""
        select max(m.messageNumber)
        from MessageJpaEntity m
        where m.chat.id = :chatId
    """)
    Long findLastMessageNumber(@Param("chatId") String chatId);
}
