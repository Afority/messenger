package com.github.messenger.domain.repository;

import com.github.messenger.domain.value_objects.FileId;

import java.util.List;

public interface FileRepository {
    void save(FileId fileId, String filename, String mimeType, long size);
    boolean existsAll(List<FileId> fileIds);
    void delete(FileId fileId);
}
