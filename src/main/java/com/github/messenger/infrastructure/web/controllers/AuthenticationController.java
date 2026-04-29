package com.github.messenger.infrastructure.web.controllers;

import com.github.messenger.domain.entity.User;
import com.github.messenger.domain.usecase.GetUserByLoginUseCase;
import com.github.messenger.domain.usecase.RegisterUserUseCase;
import com.github.messenger.domain.value_objects.Login;
import com.github.messenger.domain.value_objects.RawPassword;
import com.github.messenger.domain.value_objects.Username;
import com.github.messenger.infrastructure.security.JwtCore;
import com.github.messenger.infrastructure.web.dtos.UserCredentialsDTO;
import com.github.messenger.infrastructure.web.dtos.UserDto;
import com.github.messenger.infrastructure.web.dtos.UserLoggingInDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/auth")
@RestController
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final RegisterUserUseCase registerUserUseCase;
    private final JwtCore jwtCore;
    GetUserByLoginUseCase getUserByLoginUseCase;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager,
                                    RegisterUserUseCase registerUserUseCase,
                                    JwtCore jwtCore,
                                    GetUserByLoginUseCase getUserByLoginUseCase) {
        this.authenticationManager = authenticationManager;
        this.registerUserUseCase = registerUserUseCase;
        this.jwtCore = jwtCore;
        this.getUserByLoginUseCase = getUserByLoginUseCase;
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody UserCredentialsDTO credentials) {
        registerUserUseCase.process(
                new Login(credentials.login()),
                new Username(credentials.username()),
                new RawPassword(credentials.password())
        );

        return ResponseEntity.ok().build();
    }

    @PostMapping("/signin")
    public ResponseEntity<UserLoggingInDto> signin(@RequestBody UserCredentialsDTO credentials) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        credentials.login(),
                        credentials.password()));

        User user = getUserByLoginUseCase.execute(new Login(credentials.login()));

        UserDto userDto = new UserDto(
                user.getId().value(),
                user.getUsername().getUsernameStr()
        );
        String token = jwtCore.generateToken(authentication);

        return ResponseEntity.ok(new UserLoggingInDto(userDto, token));
    }
}
