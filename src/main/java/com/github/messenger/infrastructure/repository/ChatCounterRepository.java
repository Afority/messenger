package com.github.messenger.infrastructure.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public class ChatCounterRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public long nextMessageNumber(UUID chatId) {
        return ((Number) entityManager
                .createNativeQuery("""
                    UPDATE chats
                    SET last_message_number = last_message_number + 1
                    WHERE id = :chatId
                    RETURNING last_message_number
                """)
                .setParameter("chatId", chatId)
                .getSingleResult()).longValue();
    }
}
