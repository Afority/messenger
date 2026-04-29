package com.github.messenger.domain.usecase;

import com.github.messenger.domain.entity.User;
import com.github.messenger.domain.exceptions.UserNotFoundException;
import com.github.messenger.domain.repository.UserRepository;
import com.github.messenger.domain.value_objects.Login;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GetUserByLoginUseCase {
    private final UserRepository userRepository;

    public GetUserByLoginUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(Login userId) {
        Optional<User> user = userRepository.findByLogin(userId);
        if (user.isPresent()) {
            return user.get();
        }
        throw new UserNotFoundException();
    }
}
