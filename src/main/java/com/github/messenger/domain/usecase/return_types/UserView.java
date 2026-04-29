package com.github.messenger.domain.usecase.return_types;

import com.github.messenger.domain.value_objects.UserId;

public record UserView(
    UserId userId,
    long timestamp
) { }
