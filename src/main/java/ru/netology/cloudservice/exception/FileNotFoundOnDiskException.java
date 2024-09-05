package ru.netology.cloudservice.exception;

import org.springframework.http.HttpStatus;
import ru.netology.cloudservice.service.Ops;

public class FileNotFoundOnDiskException extends AbstractCloudException {
    public FileNotFoundOnDiskException(Ops operation, String param) {
        super(
                HttpStatus.NOT_FOUND,
                operation,
                param,
                "File %s failed (not found on Storage) file: %s"
        );
    }
}
