package com.github.messenger.infrastructure.repository;

import com.github.messenger.infrastructure.repository.entity.UserDbEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<UserDbEntity, Long> {
    Optional<UserDbEntity> findById(long id);
    Optional<UserDbEntity> findByLogin(String login);
    boolean existsByLogin(String login);
}
