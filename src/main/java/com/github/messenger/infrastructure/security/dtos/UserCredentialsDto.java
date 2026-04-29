package com.github.messenger.infrastructure.security.dtos;

public record UserCredentialsDto(
        String username, String password
) {
}
