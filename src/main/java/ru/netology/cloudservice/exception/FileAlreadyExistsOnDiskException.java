package ru.netology.cloudservice.exception;

import org.springframework.http.HttpStatus;
import ru.netology.cloudservice.service.Ops;

public class FileAlreadyExistsOnDiskException extends AbstractCloudException {
    public FileAlreadyExistsOnDiskException(Ops operation, String param) {
        super(
                HttpStatus.CONFLICT,
                operation,
                param,
                "File %s failed (already exists on Storage) file: %s"
        );
    }
}
