package ru.netology.cloudservice.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.netology.cloudservice.model.ResultMessageDto;

@RestControllerAdvice(annotations = CloudExceptionHandlerAdvice.class)
public class ControllerExceptionHandlerAdvice {

    private ResponseEntity<ResultMessageDto> r(AbstractCloudException e) {
        HttpStatus status = e.status;
        ResultMessageDto error = e.getError();
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(FileAlreadyExistsOnDiskException.class)
    public ResponseEntity<ResultMessageDto> alreadyExistsOnDiskErrorHandler(FileAlreadyExistsOnDiskException e) {
        return r(e);
    }

    @ExceptionHandler(FileInternalServerException.class)
    public ResponseEntity<ResultMessageDto> internalServerErrorHandler(FileInternalServerException e) {
        return r(e);
    }

    @ExceptionHandler(FileNotFoundInDatabaseException.class)
    public ResponseEntity<ResultMessageDto> notFoundInDatabaseErrorHandler(FileNotFoundInDatabaseException e) {
        return r(e);
    }

    @ExceptionHandler(FileNotFoundOnDiskException.class)
    public ResponseEntity<ResultMessageDto> notFoundOnDiskErrorHandler(FileNotFoundOnDiskException e) {
        return r(e);
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<ResultMessageDto> tokenNotFoundErrorHandler(TokenNotFoundException e) {
        return r(e);
    }

    @ExceptionHandler(UserNotAuthorizedException.class)
    public ResponseEntity<ResultMessageDto> tokenNotFoundErrorHandler(UserNotAuthorizedException e) {
        return r(e);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> constraintViolationHandler(ConstraintViolationException e) {
        System.out.println(e.getMessage());
        return new ResponseEntity<>(
                "ConstraintViolation Exception in Service method.\n Exception message: "
                        + e.getMessage(),
                HttpStatus.BAD_REQUEST);
    }
}
