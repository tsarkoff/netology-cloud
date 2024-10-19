package ru.netology.cloudservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import ru.netology.cloudservice.model.ResultMessageDto;
import ru.netology.cloudservice.service.Ops;

import static java.lang.String.format;

@Getter
public abstract class AbstractCloudException extends InternalError {
    protected HttpStatus status;
    protected ResultMessageDto error;

    public AbstractCloudException(
            HttpStatus status,
            Ops operation,
            String param,
            String message) {
        this.error = new ResultMessageDto(format(message, operation.getOp(), param));
        this.status = status;
    }
}
