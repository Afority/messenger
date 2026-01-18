package com.github.messenger.domain.usecase;

import com.github.messenger.domain.exceptions.UserAlreadyExistsException;
import com.github.messenger.domain.repository.UserRepository;
import com.github.messenger.domain.value_objects.Login;
import com.github.messenger.domain.value_objects.RawPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterUserUseCase {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    @Autowired
    public RegisterUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void process(Login login, RawPassword rawPassword) {
        if (userRepository.existsByLogin(login)) throw new UserAlreadyExistsException();
        userRepository.save(login, passwordEncoder.encode(rawPassword.getValue()));
    }
}
