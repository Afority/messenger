package com.github.messenger.domain.entity;

import com.github.messenger.domain.value_objects.FileId;
import lombok.Getter;

@Getter
public class Attachment {
    private final FileId fileId;
    private String filename;
    private String mimeType;
    private long size;

    public Attachment(FileId fileId, String filename, String mimeType, long size) {
        this.fileId = fileId;
        this.filename = filename;
        this.mimeType = mimeType;
        this.size = size;
    }

    public Attachment(FileId fileId) {
        this.fileId = fileId;
    }
}
