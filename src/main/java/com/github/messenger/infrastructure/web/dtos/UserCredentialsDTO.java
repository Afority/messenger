package com.github.messenger.infrastructure.web.dtos;

public record UserCredentialsDTO(String login,
                                 String username,
                                 String password) {
}
