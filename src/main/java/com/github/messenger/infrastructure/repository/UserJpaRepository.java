package com.github.messenger.infrastructure.repository;

import com.github.messenger.infrastructure.repository.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
    Optional<UserJpaEntity> findById(long id);
    Optional<UserJpaEntity> findByLogin(String login);
    boolean existsByLogin(String login);
}
