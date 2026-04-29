package com.github.messenger.infrastructure.repository;

import com.github.messenger.domain.entity.GroupChat;
import com.github.messenger.domain.value_objects.ChatId;
import com.github.messenger.infrastructure.repository.entity.ChatJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatJpaRepository extends JpaRepository<ChatJpaEntity, UUID> {
    Optional<ChatJpaEntity> findById(ChatId id);
    // todo когда нибудь изучить обход lazy через "LEFT JOIN FETCH c.members"
    @Query("""
           SELECT DISTINCT c
           FROM ChatJpaEntity c
           LEFT JOIN FETCH c.members
           JOIN ChatMemberJpaEntity cm ON cm.chat.id = c.id
           WHERE cm.user.id = :userId
           """)
    List<ChatJpaEntity> findChatIdsByUserId(@Param("userId") Long userId);

    @Query(nativeQuery = true, value="""
            SELECT EXISTS (
                SELECT 1
                FROM chats c
                JOIN chat_members cm1 ON cm1.chat_id = c.id
                JOIN chat_members cm2 ON cm2.chat_id = c.id
                WHERE c.type = 'PERSONAL'
                  AND cm1.user_id = :userId1
                  AND cm2.user_id = :userId2
            );
            """)
    boolean existsChatByParticipants(long userId1, long userId2);
}
