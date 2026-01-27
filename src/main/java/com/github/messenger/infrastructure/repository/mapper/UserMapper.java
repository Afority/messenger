package com.github.messenger.infrastructure.repository.mapper;

import com.github.messenger.domain.entity.User;
import com.github.messenger.domain.value_objects.Login;
import com.github.messenger.domain.value_objects.UserId;
import com.github.messenger.infrastructure.repository.entity.UserJpaEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class UserMapper {
    PasswordEncoder passwordEncoder;

    @Autowired
    UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User mapToEntity(UserJpaEntity userJpaEntity) {
        return new User(
                new UserId(userJpaEntity.getId()),
                new Login(userJpaEntity.getLogin()),
                userJpaEntity.getPassword(),
                userJpaEntity.isOnline(),
                Instant.ofEpochSecond(userJpaEntity.getLastSeen())
        );
    }
    public UserJpaEntity mapToDbEntity(User userEntity) {
        return new UserJpaEntity(
                userEntity.getId().value(),
                userEntity.getLogin().getValue(),
                passwordEncoder.encode(userEntity.getPassword()),
                userEntity.getLastSeen().getEpochSecond(),
                userEntity.isOnline()
        );
    }
}
