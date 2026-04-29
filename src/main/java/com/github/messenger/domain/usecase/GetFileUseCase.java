package com.github.messenger.domain.usecase;

import com.github.messenger.domain.entity.Attachment;
import com.github.messenger.domain.entity.Message;
import com.github.messenger.domain.entity.TextMessage;
import com.github.messenger.domain.exceptions.AccessDeniedException;
import com.github.messenger.domain.exceptions.FileNotFoundException;
import com.github.messenger.domain.exceptions.MessageTypeIsWrong;
import com.github.messenger.domain.repository.ChatRepository;
import com.github.messenger.domain.repository.MessageRepository;
import com.github.messenger.domain.usecase.return_types.UserFile;
import com.github.messenger.domain.value_objects.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class GetFileUseCase {
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;

    public GetFileUseCase(ChatRepository chatRepository, MessageRepository messageRepository) {
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
    }

    // возвращает файл если у пользователя есть доступ к нему
    public UserFile execute(UserId requestedUserId, FileId fileId, ChatId chatId, MessageNumber messageNumber) {
        // если пользователя нет в чате, то не имеет доступ к нему
        if (!chatRepository.userInChat(requestedUserId, chatId)) {
            throw new AccessDeniedException();
        }

        Message message = messageRepository
            .getMessage(messageNumber, chatId)
            .orElseThrow(AccessDeniedException::new);


        if (message instanceof TextMessage msg) {
            // если вложение действительно в этом сообщении
            for (Attachment attachment : msg.getAttachments()) {
                if (attachment.getFileId().equals(fileId)) {
                    String uri = "file://" + UploadFileUseCase.pathToFiles.resolve(fileId.id().toString());

                    return new UserFile(uri, attachment.getMimeType(), attachment.getSize());
                }
            }
        }
        else {
            throw new MessageTypeIsWrong("Сообщение, в котором запрашивается файл, не имеет файлов");
        }
        throw new FileNotFoundException("Файл не найден");
    }
}
