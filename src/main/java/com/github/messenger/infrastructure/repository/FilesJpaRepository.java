package com.github.messenger.infrastructure.repository;

import com.github.messenger.infrastructure.repository.entity.FileJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FilesJpaRepository extends JpaRepository<FileJpaEntity, UUID> {
}
