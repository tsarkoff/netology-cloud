package ru.netology.cloudservice.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.netology.cloudservice.exception.TokenNotFoundException;
import ru.netology.cloudservice.exception.UserNotAuthorizedException;
import ru.netology.cloudservice.model.User;
import ru.netology.cloudservice.repository.UserRepository;

import java.util.Optional;

/**
 * This TEST is specially written to avoid @SpringBootTest usage - only pure Mockito
 */
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthServiceTest {

    private AuthService authService;
    @Mock
    private UserRepository mockUserRepository;
    private final String authToken = "lex34pou5p9834u5n3span394u58u09";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthServiceImpl(mockUserRepository);
    }

    @Test
    @Order(0)
    @DisplayName("login() then findUserByCredentialsLoginIgnoreCase(username) when mockUserRepository")
    void login() {
        String username = "m@m.ru", password = "pwd", authTokenExpected = authToken;
        User user = User.builder()
                .credentials(new User.Credentials(username, password))
                .token(new User.TokenDto(authTokenExpected))
                .build();
        Mockito.doReturn(Optional.of(user)).when(mockUserRepository).findUserByCredentialsLoginIgnoreCase(username);
        String authTokenReceived = authService.login(user.getCredentials()).getAuthToken();
        Assertions.assertEquals(authTokenExpected, authTokenReceived);
        Mockito.verify(mockUserRepository, Mockito.times(1)).findUserByCredentialsLoginIgnoreCase(username);
    }

    @Test
    @Order(1)
    @DisplayName("loginThrows() then UserNotAuthorizedException & findUserByCredentialsLoginIgnoreCase()")
    void loginThrows() {
        String username = "m@m.ru";
        Assertions.assertThrows(
                UserNotAuthorizedException.class,
                () -> authService.login(new User.Credentials(username, Mockito.anyString()))
        );
        Mockito.verify(mockUserRepository, Mockito.times(1)).findUserByCredentialsLoginIgnoreCase(username);
    }

    @Test
    @Order(2)
    @DisplayName("logout() then TokenNotFoundException and findUserByTokenAuthToken()")
    void logout() {
        Assertions.assertThrows(
                TokenNotFoundException.class,
                () -> authService.logout(Optional.of(authToken))
        );
        Mockito.verify(mockUserRepository, Mockito.times(1)).findUserByTokenAuthToken(authToken);
    }

    @ParameterizedTest
    @Order(3)
    @DisplayName("validateToken() then TokenNotFoundException and findUserByTokenAuthToken()")
    @ValueSource(strings = {"", authToken})
    void validateToken(String token) {
        Assertions.assertThrows(
                TokenNotFoundException.class,
                () -> authService.validateToken(Optional.of(token))
        );
        Mockito.verify(mockUserRepository, Mockito.times(1)).findUserByTokenAuthToken(token);
    }

    @ParameterizedTest
    @Order(4)
    @DisplayName("getUserByToken() then authService.getUserByToken() and findUserByTokenAuthToken()")
    @ValueSource(strings = {"", authToken})
    void getUserByToken(String token) {
        Optional<User> user = authService.getUserByToken(Optional.of(token));
        Assertions.assertEquals(user, Optional.empty());
        Mockito.verify(mockUserRepository, Mockito.times(1)).findUserByTokenAuthToken(token);
    }

    @Test
    @Order(5)
    @DisplayName("getUsernameByToken() then authService.getUsernameByToken() фтв findUserByTokenAuthToken(authToken)")
    void getUsernameByToken() {
        String username = authService.getUsernameByToken(Optional.of(authToken));
        Assertions.assertTrue(username.isEmpty());
        Mockito.verify(mockUserRepository, Mockito.times(1)).findUserByTokenAuthToken(authToken);
    }
}