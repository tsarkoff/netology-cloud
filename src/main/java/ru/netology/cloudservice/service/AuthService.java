package ru.netology.cloudservice.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.netology.cloudservice.model.Error;
import ru.netology.cloudservice.model.User;

import java.util.Optional;

@Service
public interface AuthService {
    ResponseEntity<?> login(User.Credentials credentials);
    ResponseEntity<Error> logout(Optional<String> authToken);
    ResponseEntity<Error> validateToken(Optional<String> token);
}
