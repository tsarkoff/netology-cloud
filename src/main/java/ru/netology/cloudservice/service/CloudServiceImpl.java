package ru.netology.cloudservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudservice.config.AppProps;
import ru.netology.cloudservice.exception.CloudExceptionHandlerAdvice;
import ru.netology.cloudservice.exception.FileNotFoundInDatabaseException;
import ru.netology.cloudservice.exception.TokenNotFoundException;
import ru.netology.cloudservice.exception.UserNotAuthorizedException;
import ru.netology.cloudservice.model.Error;
import ru.netology.cloudservice.model.Item;
import ru.netology.cloudservice.model.User;
import ru.netology.cloudservice.repository.ItemRepository;
import ru.netology.cloudservice.repository.UserRepository;
import ru.netology.cloudservice.storage.IoTypes;
import ru.netology.cloudservice.storage.Storage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@CloudExceptionHandlerAdvice
public class CloudServiceImpl implements CloudService {
    private final Storage storage;
    private final AppProps props;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    // public ResponseEntity<?> login(User.Credentials credentials) {
    public ResponseEntity<User.Token> login(User.Credentials credentials) {
        String login = credentials.getLogin();
        Optional<User> user = userRepository.findUserByCredentialsLoginIgnoreCase(login);
        if (user.isEmpty() || !user.get().getCredentials().getPassword().equals(credentials.getPassword()))
            throw new UserNotAuthorizedException(Ops.LOGIN_FAILED, credentials.getLogin());
        if (user.get().getToken().getAuthToken().isEmpty()) {
            String authToken = AuthTokenGenerator.generateNewToken();
            user.get().getToken().setAuthToken(authToken);
            userRepository.save(user.get());
        }
        return ResponseEntity.ok().body(user.get().getToken());
    }

    @Override
    public ResponseEntity<Error> logout(Optional<String> token) {
        ResponseEntity<Error> response = validateToken(token);
        if (response.getStatusCode().equals(HttpStatus.OK) && token.isPresent()) {
            Optional<User> user = userRepository.findUserByTokenAuthToken(fitToken(token.get()));
            user.ifPresent(usr -> usr.getToken().invalidateToken(userRepository, usr));
        }
        return response;
    }

    @Override
    // public ResponseEntity<?> getItemList(Optional<Integer> limit, Optional<String> token) {
    public ResponseEntity<List<Item.File>> getItemList(Optional<Integer> limit, Optional<String> token) {
        validateToken(token);
        List<Item> items = itemRepository.findItemByOrderByFileSizeAsc(limit.map(Limit::of).orElseGet(Limit::unlimited));
        List<Item.File> files = items.stream().map(Item::getFile).toList();
        return ResponseEntity.ok().body(files);
    }

    @Override
    // public ResponseEntity<?> downloadItem(String filename, Optional<String> token) {
    public ResponseEntity<Object> downloadItem(String filename, Optional<String> token) {
        validateToken(token);
        if (itemRepository.findItemByFileFilename(filename).isEmpty())
            throw new FileNotFoundInDatabaseException(Ops.FILE_DOWNLOAD, filename);
        Object file = storage.read(filename);
        return ResponseEntity.ok()
                .contentType(
                        props.getStorageIoType().equals(IoTypes.MULTIPART)
                                ? MediaType.MULTIPART_MIXED
                                : MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(filename).toString())
                .body(file);
    }

    @Override
    public ResponseEntity<Error> saveItem(String filename, MultipartFile file, Optional<String> token) {
        validateToken(token);
        storage.write(file);
        Item.File itemFile = Item.File.builder().filename(file.getOriginalFilename()).size(file.getSize()).build();
        Item item = Item.builder().file(itemFile).hash(itemFile.hashCode()).build();
        itemRepository.save(item);
        return ResponseEntity.ok().body(new Error("File upload success, file: " + filename));
    }

    @Override
    public ResponseEntity<Error> updateItem(String oldFilename, String newFilename, Optional<String> token) {
        validateToken(token);
        Optional<Item> item = itemRepository.findItemByFileFilename(oldFilename);
        if (item.isEmpty())
            throw new FileNotFoundInDatabaseException(Ops.FILE_UPDATE, oldFilename);
        storage.rename(oldFilename, newFilename);
        item.get().getFile().setFilename(newFilename);
        itemRepository.save(item.get());
        return ResponseEntity.ok().body(new Error("File update success, new filename: " + newFilename));
    }

    @Override
    public ResponseEntity<Error> deleteItem(String filename, Optional<String> token) {
        validateToken(token);
        Optional<Item> item = itemRepository.findItemByFileFilename(filename);
        if (item.isEmpty())
            throw new FileNotFoundInDatabaseException(Ops.FILE_DELETE, filename);
        storage.delete(filename);
        item.ifPresent(itemRepository::delete);
        return ResponseEntity.ok().body(new Error("File delete success, file: " + filename));
    }

    private ResponseEntity<Error> validateToken(Optional<String> token) {
        if (!props.isCheckAuthToken())
            return ResponseEntity.ok().body(new Error("Request auth success (App.Props skips auth token verification)"));
        if (token.isEmpty())
            throw new TokenNotFoundException(Ops.TOKEN_HEADER_ABSENT, "NULL");
        if (userRepository.findUserByTokenAuthToken(fitToken(token.get())).isEmpty())
            throw new TokenNotFoundException(Ops.TOKEN_NOT_FOUND_IN_DB, token.get());
        return ResponseEntity.ok().body(new Error("Request auth success (auth token recognized) user: " + token.get()));
    }

    private String fitToken(String token) {
        return token.replace("Bearer ", "");
    }
}
