package com.github.messenger.infrastructure.web.dtos;

public record UserLoggingInDto(UserDto user, String token) {
}
