package ru.netology.cloudservice.storage;

import org.springframework.web.multipart.MultipartFile;

public interface Storage {
    Object read(String filename, String owner);
    void write(MultipartFile file, String owner);
    void rename(String oldFilename, String newFilename, String owner);
    void delete(String filename, String owner);
}
