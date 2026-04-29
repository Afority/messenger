package com.github.messenger.infrastructure.web.controllers;

import com.github.messenger.domain.exceptions.DomainException;
import com.github.messenger.domain.usecase.GetFileUseCase;
import com.github.messenger.domain.usecase.UploadFileUseCase;
import com.github.messenger.domain.usecase.return_types.UserFile;
import com.github.messenger.domain.value_objects.*;
import com.github.messenger.infrastructure.security.UserDetailsImpl;
import com.github.messenger.infrastructure.web.adapters.FileWrapperAdapter;
import com.github.messenger.infrastructure.web.response.FilesIdResponse;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RequestMapping("/api/files")
@RestController
public class FileController {
    private final UploadFileUseCase uploadFileUseCase;
    private final GetFileUseCase sendFileUseCase;

    public FileController(UploadFileUseCase uploadFileUseCase,
                          GetFileUseCase sendFileUseCase) {
        this.uploadFileUseCase = uploadFileUseCase;
        this.sendFileUseCase = sendFileUseCase;
    }

    @PostMapping
    public ResponseEntity<FilesIdResponse> upload(Authentication authentication,
                                                  @RequestParam("file") MultipartFile file) throws IOException {
        if (file == null) throw new DomainException("file is null");

        FileWrapperAdapter adapter = new FileWrapperAdapter(file);

        FileId fileId = uploadFileUseCase.upload(adapter);

        return ResponseEntity.ok(new FilesIdResponse(fileId.id().toString()));
    }

    @GetMapping
    public ResponseEntity<Resource> downloadFile(
        Authentication authentication,
        @RequestParam("fileId") String fileId,
        @RequestParam("chatId") String chatId,
        @RequestParam("messageId") long messageId) throws IOException {
        System.out.println(fileId);
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
        if (user == null) throw new DomainException("unknown authentication");

        UserFile file = sendFileUseCase.execute(
            new UserId(user.getId()),
            new FileId(UUID.fromString(fileId)),
            new ChatId(UUID.fromString(chatId)),
            new MessageNumber(messageId)
        );

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(file.mimeType()))
            .contentLength(file.size())
            .cacheControl(CacheControl.maxAge(1_000_000_000L, TimeUnit.SECONDS))
            .body(UrlResource.from(file.URI())
        );
    }
}
