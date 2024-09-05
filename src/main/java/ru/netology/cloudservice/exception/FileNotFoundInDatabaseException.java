package ru.netology.cloudservice.exception;

import org.springframework.http.HttpStatus;
import ru.netology.cloudservice.service.Ops;

public class FileNotFoundInDatabaseException extends AbstractCloudException {
    public FileNotFoundInDatabaseException(Ops operation, String param) {
        super(
                HttpStatus.NOT_FOUND,
                operation,
                param,
                "File %s failed (not found in Catalogue) file: %s"
        );
    }
}
