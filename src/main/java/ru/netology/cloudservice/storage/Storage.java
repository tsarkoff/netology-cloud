package ru.netology.cloudservice.storage;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudservice.config.AppProps;
import ru.netology.cloudservice.exception.FileAlreadyExistsOnDiskException;
import ru.netology.cloudservice.exception.FileInternalServerException;
import ru.netology.cloudservice.exception.FileNotFoundOnDiskException;
import ru.netology.cloudservice.service.Ops;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
public class Storage {
    private final AppProps props;

    // Download (GET /file)
    public Object read(String filename) {
        Path path = Paths.get(props.getStoragePath() + filename);
        if (!path.toFile().exists())
            throw new FileNotFoundOnDiskException(Ops.FILE_DOWNLOAD, filename);
        Object data = null;
        try (InputStream is = new FileInputStream(path.toFile())) {
            switch (props.getStorageIoType()) {
                case IoTypes.BYTES, IoTypes.MULTIPART -> {
                    if (ArrayUtils.isEmpty(new Object[]{data = is.readAllBytes()}))
                        throw new FileNotFoundOnDiskException(Ops.FILE_DOWNLOAD, filename);
                    data = new ByteArrayResource((byte[]) data);
                    if (props.getStorageIoType().equals(IoTypes.MULTIPART)) {
                        MultiValueMap<String, Object> multipart = new LinkedMultiValueMap<>(2);
                        multipart.add("hash", String.valueOf(Math.abs(path.toFile().hashCode())));
                        multipart.add("file", data);
                        data = multipart;
                    }
                }
                case IoTypes.STREAM ->
                        data = new InputStreamResource(new FileInputStream(path.toAbsolutePath().toString()));
            }
        } catch (IOException e) {
            throw new FileInternalServerException(Ops.FILE_DOWNLOAD, filename);
        }
        return data;
    }

    // Upload (POST /file)
    public void write(MultipartFile file) {
        File f = Paths.get(props.getStoragePath() + file.getOriginalFilename()).toFile();
        if (f.exists())
            throw new FileAlreadyExistsOnDiskException(Ops.FILE_UPLOAD, file.getOriginalFilename());
        try (FileOutputStream out = new FileOutputStream(f.getAbsolutePath())) {
            out.write(file.getBytes());
            out.flush();
        } catch (IOException e) {
            throw new FileInternalServerException(Ops.FILE_UPLOAD, file.getOriginalFilename());
        }
    }

    // Update (PUT /file)
    public void rename(String oldFilename, String newFilename) {
        File oldFile = Paths.get(props.getStoragePath() + oldFilename).toFile();
        File newFile = Paths.get(props.getStoragePath() + newFilename).toFile();
        if (!oldFile.exists())
            throw new FileNotFoundOnDiskException(Ops.FILE_UPDATE, oldFilename);
        if (newFile.exists())
            throw new FileAlreadyExistsOnDiskException(Ops.FILE_UPDATE, newFilename);
        try {
            Files.move(oldFile.toPath(), newFile.toPath());
        } catch (IOException e) {
            throw new FileInternalServerException(Ops.FILE_UPDATE, oldFilename);
        }
    }

    // Delete (DELETE /file)
    public void delete(String filename) {
        File f = Paths.get(props.getStoragePath() + filename).toFile();
        if (!f.exists())
            throw new FileNotFoundOnDiskException(Ops.FILE_DELETE, filename);
        try {
            Files.delete(f.toPath());
        } catch (IOException e) {
            throw new FileInternalServerException(Ops.FILE_DELETE, filename);
        }
    }
}
