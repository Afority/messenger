package com.github.messenger.infrastructure.repository.adapter;

import com.github.messenger.domain.repository.FileRepository;
import com.github.messenger.domain.value_objects.FileId;
import com.github.messenger.infrastructure.repository.FilesJpaRepository;
import com.github.messenger.infrastructure.repository.entity.FileJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class FilesRepositoryAdapter implements FileRepository {
    FilesJpaRepository filesJpaRepository;

    public FilesRepositoryAdapter(FilesJpaRepository filesJpaRepository) {
        this.filesJpaRepository = filesJpaRepository;
    }

    @Override
    public void save(FileId fileId, String filename, String mimeType, long size) {
        filesJpaRepository.save(new FileJpaEntity(fileId.id(), filename, mimeType, size));
    }

    @Override
    public void delete(FileId fileId) {
        filesJpaRepository.deleteById(fileId.id());
    }

    @Override
    public boolean existsAll(List<FileId> fileIds) {
        return filesJpaRepository
            .findAllById(
                fileIds.stream()
                    .map(FileId::id)
                    .toList()
            ).size() == fileIds.size();
    }
}
