package com.github.messenger.infrastructure.repository.adapter;

import com.github.messenger.domain.entity.GroupChat;
import com.github.messenger.domain.entity.PersonalChat;
import com.github.messenger.domain.entity.User;
import com.github.messenger.domain.repository.ChatRepository;
import com.github.messenger.domain.value_objects.*;
import com.github.messenger.infrastructure.repository.ChatMembersJpaRepository;
import com.github.messenger.infrastructure.repository.MessageJpaRepository;
import com.github.messenger.infrastructure.repository.MessageViewJpaRepository;
import com.github.messenger.infrastructure.repository.adapter.dtos.GetChatsResult;
import com.github.messenger.infrastructure.repository.adapter.dtos.MessagePreview;
import com.github.messenger.infrastructure.repository.entity.*;
import com.github.messenger.infrastructure.repository.mapper.ChatMapper;
import com.github.messenger.infrastructure.repository.ChatJpaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ChatRepositoryAdapter implements ChatRepository {
    ChatJpaRepository chatJpaRepository;
    ChatMembersJpaRepository chatMembersJpaRepository;
    MessageJpaRepository messageJpaRepository;
    MessageViewJpaRepository messageViewJpaRepository;
    ChatMapper chatMapper;

    public ChatRepositoryAdapter(ChatJpaRepository chatJpaRepository,
                                 ChatMembersJpaRepository chatMembersJpaRepository,
                                 ChatMapper chatMapper,
                                 MessageJpaRepository messageJpaRepository,
                                 MessageViewJpaRepository messageViewJpaRepository) {
        this.chatJpaRepository = chatJpaRepository;
        this.chatMembersJpaRepository = chatMembersJpaRepository;
        this.chatMapper = chatMapper;
        this.messageJpaRepository = messageJpaRepository;
        this.messageViewJpaRepository = messageViewJpaRepository;
    }

    @Override
    @Transactional
    public ChatId savePersonalChat(UserId creatorId, UserId participantId) {
        ChatJpaEntity savedChat = chatJpaRepository.save(
            new ChatJpaEntity(null, null, null,
                ChatType.PERSONAL, null, 0L)
        );

        List<UserId> members = List.of(creatorId, participantId);

        chatMembersJpaRepository.saveAll(
            members.stream().map(memberId ->
                new ChatMemberJpaEntity(new UserJpaEntity(memberId.value()), savedChat)).toList()
        );
        return new ChatId(savedChat.getId());
    }

    @Override
    @Transactional
    public ChatId saveGroupChat(List<UserId> participants, String name, String description) {
        ChatJpaEntity savedChat = chatJpaRepository.save(
            new ChatJpaEntity(null, name, description, ChatType.GROUP, null, 0L)
        );

        chatMembersJpaRepository.saveAll(
            participants.stream().map(memberId ->
                new ChatMemberJpaEntity(new UserJpaEntity(memberId.value()), savedChat)).toList()
        );
        return new ChatId(savedChat.getId());
    }

    @Override
    public Optional<PersonalChat> findPersonalChat(ChatId chatId) {
        Optional<ChatJpaEntity> chat = chatJpaRepository.findById(chatId.value());

        if (chat.isPresent()) {
            List<UserId> members =
                chatMembersJpaRepository.getChatMembers(chatId.value())
                    .stream()
                    .map(member -> new UserId(member.getUser().getId()))
                    .toList();


            return Optional.of(new PersonalChat(chatId, members, chat.get().getLastMessageNumber()));

        }
        return Optional.empty();
    }

    @Override
    public boolean chatExists(ChatId chatId) {
        return chatJpaRepository.existsById(chatId.value());
    }

    @Override
    public List<GetChatsResult> getChats(UserId userId) {
        List<ChatJpaEntity> chats = chatJpaRepository.findChatIdsByUserId(userId.value());
        List<GetChatsResult> result = new ArrayList<>();

        for (ChatJpaEntity chat : chats) {
            MessageJpaEntity lastMessage = messageJpaRepository.getLastMessage(chat.getId());

            long unreadCount = messageViewJpaRepository.getCountUnreadMessages(chat.getId(), userId.value());

            FileJpaEntity photo = chat.getPhoto();
            FileId photoId = photo == null ? null : new FileId(photo.getId());

            List<UserId> participantsIDs = chat.getMembers().stream()
                .map(participant -> new UserId(participant.getUser().getId()))
                .toList();

            List<User> participants = chat.getMembers().stream()
                .map(ChatMemberJpaEntity::getUser)
                .map(member -> new User(
                    new UserId(member.getId()),
                    new Login(member.getLogin()),
                    new Username(member.getUsername()),
                    member.getPassword(),
                    member.isOnline(),
                    Instant.ofEpochSecond(member.getLastSeen())
                ))
                .toList();

            MessagePreview lastMessagePreview = lastMessage == null ? null :
                new MessagePreview(
                    lastMessage.getSender().getId(),
                    lastMessage.getText(),
                    lastMessage.getSendingTime()
                );

            if (chat.getType() == ChatType.GROUP) {
                result.add(
                    new GetChatsResult(new GroupChat(
                        new ChatId(chat.getId()),
                        chat.getName(),
                        chat.getDescription(),
                        photoId,
                        participantsIDs,
                        chat.getLastMessageNumber()
                    ),
                        participants,
                        lastMessagePreview,
                        unreadCount
                    )
                );
            }
            if (chat.getType() == ChatType.PERSONAL) {
                result.add(
                    new GetChatsResult(
                        new PersonalChat(
                            new ChatId(chat.getId()),
                            participantsIDs,
                            chat.getLastMessageNumber()
                        ),
                        participants,
                        lastMessagePreview,
                        unreadCount
                    )
                );
            }
        }
        return result;
    }

    @Override
    public boolean privateChatExists(UserId userId1, UserId userId2) {
        return chatJpaRepository.existsChatByParticipants(userId1.value(), userId2.value());
    }

    @Override
    public boolean userInChat(UserId userId, ChatId chatId) {
        return chatMembersJpaRepository.findByChatIdAndUserId(chatId.value(), userId.value()) != null;
    }
}
