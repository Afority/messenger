package com.github.messenger.infrastructure.repository.adapter.dtos;

public record MessagePreview(
    long senderId,
    String messageText,
    long sendingTime
) { }
