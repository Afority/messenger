package com.github.messenger.domain.repository;

import java.util.Optional;
import com.github.messenger.domain.entity.UserEntity;
import com.github.messenger.domain.value_objects.Login;
import com.github.messenger.domain.value_objects.UserId;

public interface UserRepository {
    Optional<UserEntity> findById(UserId id);
    Optional<UserEntity> findByLogin(Login login);
    void save(Login login, String hashedPassword);
    boolean existsByLogin(Login login);
}
