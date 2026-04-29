package com.github.messenger.domain.repository;

import java.util.List;
import java.util.Optional;
import com.github.messenger.domain.entity.User;
import com.github.messenger.domain.value_objects.Login;
import com.github.messenger.domain.value_objects.UserId;
import com.github.messenger.domain.value_objects.Username;

public interface UserRepository {
    Optional<User> findById(UserId id);
    Optional<User> findByLogin(Login login);
    List<User> findByLikeUsername(Username username);
    List<User> findAllByIds(List<UserId> ids);
    void save(Login login, Username username, String hashedPassword);
    boolean existsByLogin(Login login);
    boolean existsById(UserId id);
}
