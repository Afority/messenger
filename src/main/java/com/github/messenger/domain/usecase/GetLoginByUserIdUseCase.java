package com.github.messenger.domain.usecase;

import com.github.messenger.domain.entity.User;
import com.github.messenger.domain.exceptions.UserNotFoundException;
import com.github.messenger.domain.repository.UserRepository;
import com.github.messenger.domain.value_objects.Login;
import com.github.messenger.domain.value_objects.UserId;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GetLoginByUserIdUseCase {
    private final UserRepository userRepository;

    public GetLoginByUserIdUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Login execute(UserId userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return user.get().getLogin();
        }
        throw new UserNotFoundException(userId);
    }
}
