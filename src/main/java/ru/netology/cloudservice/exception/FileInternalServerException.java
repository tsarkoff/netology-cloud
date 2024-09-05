package ru.netology.cloudservice.exception;

import org.springframework.http.HttpStatus;
import ru.netology.cloudservice.service.Ops;

public class FileInternalServerException extends AbstractCloudException {
    public FileInternalServerException(Ops operation, String param) {
        super(HttpStatus.INTERNAL_SERVER_ERROR,
                operation,
                param,
                "File %s failed (internal error) file: %s"
        );
    }
}
