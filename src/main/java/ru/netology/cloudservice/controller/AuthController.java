package ru.netology.cloudservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netology.cloudservice.exception.CloudExceptionHandlerAdvice;
import ru.netology.cloudservice.logging.Logback;
import ru.netology.cloudservice.model.Error;
import ru.netology.cloudservice.model.User;
import ru.netology.cloudservice.service.AuthService;

import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping
@RequiredArgsConstructor
@CloudExceptionHandlerAdvice // all exceptions  handled by ExceptionHandlerAdvice (might be several of Handlers)
public class AuthController {
    private final AuthService authService;

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@Valid @RequestBody User.Credentials credentials) {
        return Logback.log(authService.login(credentials));
    }

    @PostMapping("/logout")
    public ResponseEntity<Error> logout(@RequestHeader("auth-token") Optional<String> headerAuthToken) {
        return Logback.log(authService.logout(headerAuthToken));
    }
}
