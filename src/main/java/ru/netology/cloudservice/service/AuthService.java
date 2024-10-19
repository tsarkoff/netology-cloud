package ru.netology.cloudservice.service;

import org.springframework.stereotype.Service;
import ru.netology.cloudservice.model.ResultMessageDto;
import ru.netology.cloudservice.model.User;

import java.util.Optional;

@Service
public interface AuthService {
    User.TokenDto login(User.Credentials credentials);
    ResultMessageDto logout(Optional<String> authToken);
    ResultMessageDto validateToken(Optional<String> token);
}
