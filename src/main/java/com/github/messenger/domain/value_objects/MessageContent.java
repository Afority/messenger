package com.github.messenger.domain.value_objects;

import com.github.messenger.domain.exceptions.MessageIsTooLong;
import lombok.Getter;

@Getter
public class MessageContent {
    // Если файл или картинка, то в content пишется имя файла в файловой системе
    // Если текст, то в content пишется текст
    private MessageContentType type;
    private byte[] content;

    public MessageContent(MessageContentType type, byte[] content) {
        validate(type, content);

        this.type = type;
        this.content = content;
    }

    public void edit(byte[] newContent) {
        validate(type, newContent);
        this.content = newContent;
    }

    private void validate(MessageContentType type, byte[] content) {
        if (content == null && (type == MessageContentType.TEXT || type == MessageContentType.FILE))
            throw new IllegalArgumentException("Content cannot be null");

        if (content != null && !(type == MessageContentType.TEXT || type == MessageContentType.FILE))
            throw new IllegalArgumentException("There should be no content");

        if (type == MessageContentType.TEXT && content.length > 4000)
            throw new MessageIsTooLong();
    }
}
