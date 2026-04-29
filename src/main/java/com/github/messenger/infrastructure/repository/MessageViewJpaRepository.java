package com.github.messenger.infrastructure.repository;

import com.github.messenger.infrastructure.repository.entity.MessageJpaEntity;
import com.github.messenger.infrastructure.repository.entity.MessageViewJpaEntity;
import com.github.messenger.infrastructure.repository.entity.UserJpaEntity;
import com.github.messenger.infrastructure.repository.return_types.MessageViewRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface MessageViewJpaRepository extends JpaRepository<MessageViewJpaEntity, Long> {
    @Query(value = """
            SELECT (select count(*) from messages WHERE chat_id = :chatId) - count(*)
            FROM messages_views mv
            join messages m ON m.id = mv.message_id
            join chats c on c.id = m.chat_id
            WHERE mv.user_id = :userId and c.id = :chatId
            """, nativeQuery = true)
    long getCountUnreadMessages(UUID chatId, long userId);

    @Query("""
           SELECT new com.github.messenger.infrastructure.repository.return_types.
                      MessageViewRow(mv.message.id, mv.user.id)
           FROM MessageViewJpaEntity mv
           WHERE mv.message.id IN :messageId
           """)
    List<MessageViewRow> findByMessageIdIn(Collection<Long> messageId);

    // MessageViewJpaEntity findByUserAndMessage(UserJpaEntity user, MessageJpaEntity message);
}
