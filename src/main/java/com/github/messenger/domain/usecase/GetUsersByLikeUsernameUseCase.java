package com.github.messenger.domain.usecase;

import com.github.messenger.domain.entity.User;
import com.github.messenger.domain.repository.UserRepository;
import com.github.messenger.domain.value_objects.Username;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetUsersByLikeUsernameUseCase {
    private final UserRepository userRepository;

    public GetUsersByLikeUsernameUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> execute(Username username) {
        return userRepository.findByLikeUsername(username);
    }
}
