package com.github.messenger.infrastructure.repository.adapter;

import com.github.messenger.domain.entity.User;
import com.github.messenger.domain.repository.UserRepository;
import com.github.messenger.domain.value_objects.Login;
import com.github.messenger.domain.value_objects.UserId;
import com.github.messenger.infrastructure.mapper.UserMapper;
import com.github.messenger.infrastructure.repository.UserJpaRepository;
import com.github.messenger.infrastructure.repository.entity.UserJpaEntity;
import com.github.messenger.infrastructure.repository.entity.UserPresenceJpaEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserRepositoryAdapter implements UserRepository {
    private UserJpaRepository userJpaRepository;
    private UserMapper userMapper;

    @Autowired
    public UserRepositoryAdapter(UserJpaRepository userJpaRepository, UserMapper userMapper) {
        this.userJpaRepository = userJpaRepository;
        this.userMapper = userMapper;
    }

    @Override
    public Optional<User> findById(UserId id) {
        return userJpaRepository
                .findById(id.value())
                .map(dbEntity -> userMapper.mapToEntity(dbEntity));
    }

    @Override
    public Optional<User> findByLogin(Login login) {
        return userJpaRepository
                .findByLogin(login.getValue())
                .map(dbEntity -> userMapper.mapToEntity(dbEntity));
    }

    @Override
    public void save(Login login, String hashedPassword) {
        userJpaRepository.save(new UserJpaEntity(login.getValue(), hashedPassword, new UserPresenceJpaEntity()));
    }

    @Override
    public boolean existsByLogin(Login login) {
        return userJpaRepository.existsByLogin(login.getValue());
    }
}
