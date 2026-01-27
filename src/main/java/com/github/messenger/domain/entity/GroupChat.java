package com.github.messenger.domain.entity;

import com.github.messenger.domain.value_objects.ChatId;
import com.github.messenger.domain.value_objects.ChatType;
import com.github.messenger.domain.value_objects.FileId;
import com.github.messenger.domain.value_objects.UserId;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@Getter
public class GroupChat {
    private final ChatId chatId;
    private List<UserId> participants;
    private String name;
    private @Nullable String description;
    private @Nullable FileId photo;

    public GroupChat(ChatId chatId, String name, @Nullable String description, @Nullable FileId photo, List<UserId> participants) {
        Objects.requireNonNull(chatId);
        Objects.requireNonNull(name);
        Objects.requireNonNull(participants);

        if (participants.isEmpty())
            throw new IllegalStateException("В чате должен быть хотя бы 1 участник");

        this.chatId = chatId;
        this.name = name;
        this.description = description;
        this.photo = photo;
        this.participants = participants;
    }
}
