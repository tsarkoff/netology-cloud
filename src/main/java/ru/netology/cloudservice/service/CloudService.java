package ru.netology.cloudservice.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudservice.model.Error;
import ru.netology.cloudservice.model.User;

import java.util.Optional;

@Service
public interface CloudService {
    ResponseEntity<?> login(User.Credentials credentials);

    ResponseEntity<Error> logout(Optional<String> authToken);

    ResponseEntity<Error> saveItem(String itemName, MultipartFile document, Optional<String> token);

    ResponseEntity<?> getItemList(Optional<Integer> limit, Optional<String> token);

    ResponseEntity<Error> updateItem(String oldFilename, String newFilename, Optional<String> token);

    ResponseEntity<Error> deleteItem(String filename, Optional<String> token);

    ResponseEntity<?> downloadItem(String filename, Optional<String> token);
}
