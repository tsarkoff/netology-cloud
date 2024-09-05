package ru.netology.cloudservice.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.netology.cloudservice.model.Error;

@RestControllerAdvice(annotations = CloudExceptionHandlerAdvice.class)
public class ControllerExceptionHandlerAdvice {

    ResponseEntity<Error> r(AbstractCloudException e) {
        HttpStatus status = e.status;
        Error error = e.getError();
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(FileAlreadyExistsOnDiskException.class)
    public ResponseEntity<Error> alreadyExistsOnDiskErrorHandler(FileAlreadyExistsOnDiskException e) {
        return r(e);
    }

    @ExceptionHandler(FileInternalServerException.class)
    public ResponseEntity<Error> internalServerErrorHandler(FileInternalServerException e) {
        return r(e);
    }

    @ExceptionHandler(FileNotFoundInDatabaseException.class)
    public ResponseEntity<Error> notFoundInDatabaseErrorHandler(FileNotFoundInDatabaseException e) {
        return r(e);
    }

    @ExceptionHandler(FileNotFoundOnDiskException.class)
    public ResponseEntity<Error> notFoundOnDiskErrorHandler(FileNotFoundOnDiskException e) {
        return r(e);
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<Error> tokenNotFoundErrorHandler(TokenNotFoundException e) {
        return r(e);
    }

    @ExceptionHandler(UserNotAuthorizedException.class)
    public ResponseEntity<Error> tokenNotFoundErrorHandler(UserNotAuthorizedException e) {
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
