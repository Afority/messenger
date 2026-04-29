package com.github.messenger.infrastructure.web.adapters;

import com.github.messenger.domain.usecase.interfaces.FileInWrapper;
import org.jspecify.annotations.Nullable;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class FileWrapperAdapter implements FileInWrapper, MultipartFile {
    MultipartFile file;

    public FileWrapperAdapter(MultipartFile file) {
        this.file = file;
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public @Nullable String getOriginalFilename() {
        return file.getOriginalFilename();
    }

    @Override
    public @Nullable String getContentType() {
        return file.getContentType();
    }

    @Override
    public boolean isEmpty() {
        return file.isEmpty();
    }

    @Override
    public long getSize() {
        return file.getSize();
    }

    @Override
    public byte[] getBytes() throws IOException {
        return file.getBytes();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return file.getInputStream();
    }

    @Override
    public Resource getResource() {
        return file.getResource();
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        file.transferTo(dest);
    }

    @Override
    public void transferTo(Path dest) throws IOException, IllegalStateException {
        file.transferTo(dest);
    }
}
