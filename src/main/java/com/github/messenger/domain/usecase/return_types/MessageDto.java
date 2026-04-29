package com.github.messenger.domain.usecase.return_types;

import com.github.messenger.domain.entity.Message;
import com.github.messenger.domain.value_objects.UserId;

import java.util.List;

public record MessageDto(Message message, List<UserId> viewedUsers) { }
