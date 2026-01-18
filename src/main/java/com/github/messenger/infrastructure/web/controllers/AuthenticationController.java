package com.github.messenger.infrastructure.web.controllers;

import com.github.messenger.domain.usecase.RegisterUserUseCase;
import com.github.messenger.domain.value_objects.Login;
import com.github.messenger.domain.value_objects.RawPassword;
import com.github.messenger.infrastructure.security.JwtCore;
import com.github.messenger.infrastructure.web.dtos.UserCredentialsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    AuthenticationManager authenticationManager;
    private RegisterUserUseCase registerUserUseCase;
    JwtCore jwtCore;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, RegisterUserUseCase registerUserUseCase, JwtCore jwtCore) {
        this.authenticationManager = authenticationManager;
        this.registerUserUseCase = registerUserUseCase;
        this.jwtCore = jwtCore;
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody UserCredentialsDTO credentials) {
        registerUserUseCase.process(new Login(credentials.login()), new RawPassword(credentials.password()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signin")
    public ResponseEntity<String> signin(@RequestBody UserCredentialsDTO credentials) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        credentials.login(),
                        credentials.password()));

        return ResponseEntity.ok(jwtCore.generateToken(authentication));
    }
}
