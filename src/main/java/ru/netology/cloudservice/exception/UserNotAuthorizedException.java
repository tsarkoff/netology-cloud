package ru.netology.cloudservice.exception;

import org.springframework.http.HttpStatus;
import ru.netology.cloudservice.service.Ops;

public class UserNotAuthorizedException extends AbstractCloudException {
    public UserNotAuthorizedException(Ops operation, String param) {
        super(
                HttpStatus.UNAUTHORIZED,
                operation,
                param,
                "Login failed (%s) user: %s"
        );
    }
}
