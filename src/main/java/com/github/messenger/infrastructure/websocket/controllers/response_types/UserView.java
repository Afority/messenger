package com.github.messenger.infrastructure.websocket.controllers.response_types;

public record UserView(
    long userId,
    long timestamp
) { }
