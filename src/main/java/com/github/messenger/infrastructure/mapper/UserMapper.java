package com.github.messenger.infrastructure.mapper;

import com.github.messenger.domain.entity.UserEntity;
import com.github.messenger.domain.value_objects.Login;
import com.github.messenger.domain.value_objects.UserId;
import com.github.messenger.infrastructure.repository.entity.UserDbEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    PasswordEncoder passwordEncoder;

    @Autowired
    UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity mapToEntity(UserDbEntity userDbEntity) {
        return new UserEntity(
                new UserId(userDbEntity.getId()),
                new Login(userDbEntity.getLogin()),
                userDbEntity.getPassword()
        );
    }
    public UserDbEntity mapToDbEntity(UserEntity userEntity) {
        return new UserDbEntity(
                userEntity.getId(),
                userEntity.getLogin(),
                passwordEncoder.encode(userEntity.getPassword())
        );
    }
}
