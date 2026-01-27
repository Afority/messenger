package com.github.messenger.infrastructure.security;

import com.github.messenger.infrastructure.repository.UserJpaRepository;
import com.github.messenger.infrastructure.repository.entity.UserJpaEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserJpaRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserJpaRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        UserJpaEntity user = userRepository.findByLogin(login).orElseThrow(
                () -> new UsernameNotFoundException(String.format("User %s not found", login)));

        return new UserDetailsImpl(user.getId(), login, user.getPassword());
    }

    public UserDetails loadUserById(Long id) throws UsernameNotFoundException {
        UserJpaEntity user = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException(String.format("User %s not found", id))
        );
        return new UserDetailsImpl(user.getId(), user.getLogin(), user.getPassword());
    }
}
