package ru.netology.cloudservice.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class StorageDataBase implements Storage {

    // Download (GET /file)
    @Override
    public Object read(String filename, String owner) {
        return null;
    }

    // Upload (POST /file)
    @Override
    public void write(MultipartFile file, String owner) {
    }

    // Update (PUT /file)
    @Override
    public void rename(String oldFilename, String newFilename, String owner) {
    }

    // Delete (DELETE /file)
    @Override
    public void delete(String filename, String owner) {
    }
}
