package com.github.messenger.infrastructure.repository.entity;

import com.github.messenger.domain.value_objects.ChatType;
import jakarta.persistence.*;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.List;

@Entity
@Table(name = "chats")
@Data
public class ChatJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatType type;

    @ManyToOne
    @JoinColumn(name = "photo_id")
    private FileJpaEntity photo;

    @OneToMany(mappedBy = "chat")
    private List<ChatMemberJpaEntity> members;

    @Column(name="last_message_number", nullable = false)
    private Long lastMessageNumber;

    public ChatJpaEntity(UUID id, @Nullable String name, @Nullable String description,
                         ChatType type, @Nullable FileJpaEntity photo, Long lastMessageNumber) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.photo = photo;
        this.lastMessageNumber = lastMessageNumber;
    }

    public ChatJpaEntity(UUID id) {
        this.id = id;
        lastMessageNumber = 0L;
    }

    public ChatJpaEntity() {
        lastMessageNumber = 0L;
    }
}
