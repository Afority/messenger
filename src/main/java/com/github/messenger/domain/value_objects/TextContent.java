package com.github.messenger.domain.value_objects;

import com.github.messenger.domain.exceptions.MessageIsEmptyException;
import com.github.messenger.domain.exceptions.MessageIsTooLong;

import java.util.Objects;

public class TextContent {
    private String text;

    public String getText() {
        return text;
    }

    public TextContent(String text) {
        validate(text);
        this.text = text;
    }

    private void validate(String text){
        Objects.requireNonNull(text);

        if (text.isEmpty())
            throw new MessageIsEmptyException();
        if (text.length() > 4000)
            throw new MessageIsTooLong();
    }

}
