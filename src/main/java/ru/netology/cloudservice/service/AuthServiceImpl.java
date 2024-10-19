package ru.netology.cloudservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.netology.cloudservice.exception.CloudExceptionHandlerAdvice;
import ru.netology.cloudservice.exception.TokenNotFoundException;
import ru.netology.cloudservice.exception.UserNotAuthorizedException;
import ru.netology.cloudservice.model.ResultMessageDto;
import ru.netology.cloudservice.model.User;
import ru.netology.cloudservice.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@CloudExceptionHandlerAdvice
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;

    @Override
    public User.TokenDto login(User.Credentials credentials) {
        String login = credentials.getLogin();
        Optional<User> user = userRepository.findUserByCredentialsLoginIgnoreCase(login);
        if (user.isEmpty() || !user.get().getCredentials().getPassword().equals(credentials.getPassword()))
            throw new UserNotAuthorizedException(Ops.LOGIN_FAILED, credentials.getLogin());
        if (user.get().getToken().getAuthToken().isEmpty()) {
            user.get().getToken().generateNewToken(userRepository, user.get());
            userRepository.save(user.get());
        }
        return user.get().getToken();
    }

    @Override
    public ResultMessageDto logout(Optional<String> token) {
        ResultMessageDto result = validateToken(token);
        User user = userRepository.findUserByTokenAuthToken(fitToken(token.get())).get();
        user.getToken().invalidateToken(userRepository, user);
        return result;
    }

    @Override
    public ResultMessageDto validateToken(Optional<String> token) {
        if (token.isEmpty())
            throw new TokenNotFoundException(Ops.TOKEN_HEADER_ABSENT, "NULL");
        if (userRepository.findUserByTokenAuthToken(fitToken(token.get())).isEmpty())
            throw new TokenNotFoundException(Ops.TOKEN_NOT_FOUND_IN_DB, token.get());
        return new ResultMessageDto("Request success (auth token recognized) user: " + token.get());
    }

    private String fitToken(String token) {
        return token.replace("Bearer ", "");
    }
}
