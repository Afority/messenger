package com.github.messenger.domain.repository;

import java.util.Optional;
import com.github.messenger.domain.entity.User;
import com.github.messenger.domain.value_objects.Login;
import com.github.messenger.domain.value_objects.UserId;

public interface UserRepository {
    Optional<User> findById(UserId id);
    Optional<User> findByLogin(Login login);
    void save(Login login, String hashedPassword);
    boolean existsByLogin(Login login);
    boolean existsById(UserId id);
}
