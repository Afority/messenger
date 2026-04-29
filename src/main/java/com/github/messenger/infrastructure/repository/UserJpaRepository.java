package com.github.messenger.infrastructure.repository;

import com.github.messenger.infrastructure.repository.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
    Optional<UserJpaEntity> findById(long id);
    Optional<UserJpaEntity> findByLogin(String login);

    @Query("""
            SELECT user
            FROM UserJpaEntity user
            WHERE user.username LIKE :username
            """)
    List<UserJpaEntity> findAllByLikeUsername(String username);
    List<UserJpaEntity> findAllByIdIn(List<Long> ids);

    boolean existsByLogin(String login);
}
