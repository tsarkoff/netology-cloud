package ru.netology.cloudservice.exception;

import org.springframework.http.HttpStatus;
import ru.netology.cloudservice.service.Ops;

public class TokenNotFoundException extends AbstractCloudException {
    public TokenNotFoundException(Ops operation, String param) {
        super(
                HttpStatus.NOT_FOUND,
                operation,
                param,
                "Request failed (%s) token: %s");
    }
}
