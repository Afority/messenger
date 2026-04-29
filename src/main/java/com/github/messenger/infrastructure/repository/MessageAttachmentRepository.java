package com.github.messenger.infrastructure.repository;

import com.github.messenger.infrastructure.repository.entity.MessageAttachmentId;
import com.github.messenger.infrastructure.repository.entity.MessageAttachmentJpaEntity;
import org.springframework.data.repository.CrudRepository;

public interface MessageAttachmentRepository extends CrudRepository<MessageAttachmentJpaEntity, MessageAttachmentId> {
}
