package com.github.messenger.infrastructure.repository.entity;

import com.github.messenger.domain.value_objects.ForwardType;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "messages")
@Getter
public class MessageJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_number", nullable = false)
    private Long messageNumber;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private UserJpaEntity sender;

    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    private ChatJpaEntity chat;

    @Column(nullable = false)
    private Long sendingTime;

    @Column(nullable = false)
    private Boolean edited;

    @Column(name="last_edited_time")
    private Long lastEditedTime;

    @Column(nullable = false)
    private Boolean deleted;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "message")
    List<MessageAttachmentJpaEntity> attachments;

    private String text;

    @OneToMany(mappedBy = "message")
    private List<MessageViewJpaEntity> views;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "content_type")
    private ContentType contentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "forward_type")
    private ForwardType forwardType;

    @OneToOne
    @JoinColumn(name="forward_to")
    MessageJpaEntity forwardedMessage;

    public MessageJpaEntity(Long id) {
        this.id = id;
    }

    public MessageJpaEntity() {
        attachments = new ArrayList<>();
        this.deleted = false;
        this.edited = false;
        lastEditedTime = 0L;
    }

    public MessageJpaEntity(Long messageNumber,
                            ContentType contentType,
                            UserJpaEntity sender,
                            ChatJpaEntity chat,
                            String text,
                            long sendingTime) {
        attachments = new ArrayList<>();
        this.messageNumber = messageNumber;
        this.contentType = contentType;
        this.sender = sender;
        this.chat = chat;
        this.text = text;
        this.sendingTime = sendingTime;
        this.deleted = false;
        this.edited = false;
        lastEditedTime = 0L;
    }
}
