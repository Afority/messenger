package com.github.messenger.domain.usecase;

import com.github.messenger.domain.exceptions.FileResolveTypeException;
import com.github.messenger.domain.repository.FileRepository;
import com.github.messenger.domain.usecase.interfaces.FileInWrapper;
import com.github.messenger.domain.value_objects.FileId;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class UploadFileUseCase {
    private final FileRepository filesRepository;
    public static final String filesDirName = "/app";
    public static final Path pathToFiles = Paths.get(filesDirName);
    private final Tika tika;

    public UploadFileUseCase(FileRepository filesRepository) throws IOException {
        this.filesRepository = filesRepository;
        this.tika = new Tika();

        if (!Files.exists(pathToFiles)) {
            Files.createDirectory(pathToFiles);
        }
    }

    public FileId upload(FileInWrapper userFile) throws IOException {
        FileId fileId = new FileId(UUID.randomUUID());
        Path pathToFile = pathToFiles.resolve(fileId.toString());

        File file = Files.createFile(pathToFile).toFile();

        try {
            userFile.transferTo(file);
        }
        catch (IOException | IllegalStateException e) {
            filesRepository.delete(fileId);
            throw e;
        }
        String mimeType;

        try {
            mimeType = tika.detect(file);
        }
        catch (IOException e) {
            throw new FileResolveTypeException("Не корректный тип файла");
        }

        filesRepository.save(fileId, userFile.getOriginalFilename(), mimeType, userFile.getSize());

        return fileId;
    }
}
