package com.github.messenger.domain.value_objects;

import lombok.Getter;

import java.util.List;
import java.util.Objects;

import static com.github.messenger.domain.value_objects.MessageContentType.*;

public class Message {
    @Getter
    private List<MessageContent> contents;

    public Message(List<MessageContent> contents) {
        validate(contents);
        this.contents = contents;
    }

    private void validate(List<MessageContent> contents) {
        // В сообщении не может быть несколько текстов, не больше 10 вложений
        Objects.requireNonNull(contents, "contents must not be null");

        if (contents.isEmpty())
            throw new IllegalArgumentException("contents is empty");

        if (contents.size() > 10)
            throw new IllegalArgumentException("there is much contents");

        boolean hasSingleOnlyType = contents.stream()
                .map(MessageContent::getType)
                .anyMatch(type ->
                        type == CALL ||
                        type == USER_INVITED_TO_CHAT ||
                        type == USER_LEAVE_FROM_CHAT
                );

        if (contents.size() > 1 && hasSingleOnlyType)
            throw new IllegalArgumentException("Сообщения типов CALL, USER_INVITED_TO_CHAT, USER_LEAVE_FROM_CHAT не могут иметь больше 1 вложения");


        long textCount = contents.stream()
                .filter(content -> content.getType() == MessageContentType.TEXT)
                .count();

        if (textCount > 1)
            throw new IllegalArgumentException("В сообщении не может быть более одного текста");
    }
}
