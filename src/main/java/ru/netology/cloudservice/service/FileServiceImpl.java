package ru.netology.cloudservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudservice.config.AppProps;
import ru.netology.cloudservice.exception.CloudExceptionHandlerAdvice;
import ru.netology.cloudservice.exception.FileNotFoundInDatabaseException;
import ru.netology.cloudservice.model.Error;
import ru.netology.cloudservice.model.Item;
import ru.netology.cloudservice.repository.ItemRepository;
import ru.netology.cloudservice.storage.IoTypes;
import ru.netology.cloudservice.storage.Storage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@CloudExceptionHandlerAdvice
public class FileServiceImpl implements FileService {
    private final Storage storage;
    private final AppProps props;
    private final AuthService authService;
    private final ItemRepository itemRepository;

    @Override
    public ResponseEntity<?> getItemList(Optional<Integer> limit, Optional<String> token) {
        authService.validateToken(token);
        List<Item> items = itemRepository.findItemByOrderByFileSizeAsc(limit.map(Limit::of).orElseGet(Limit::unlimited));
        List<Item.File> files = items.stream().map(Item::getFile).toList();
        return ResponseEntity.ok().body(files);
    }

    @Override
    public ResponseEntity<?> downloadItem(String filename, Optional<String> token) {
        authService.validateToken(token);
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
        authService.validateToken(token);
        storage.write(file);
        Item.File itemFile = Item.File.builder().filename(file.getOriginalFilename()).size(file.getSize()).build();
        Item item = Item.builder().file(itemFile).hash(itemFile.hashCode()).build();
        itemRepository.save(item);
        return ResponseEntity.ok().body(new Error("File upload success, file: " + filename));
    }

    @Override
    public ResponseEntity<Error> updateItem(String oldFilename, String newFilename, Optional<String> token) {
        authService.validateToken(token);
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
        authService.validateToken(token);
        Optional<Item> item = itemRepository.findItemByFileFilename(filename);
        if (item.isEmpty())
            throw new FileNotFoundInDatabaseException(Ops.FILE_DELETE, filename);
        storage.delete(filename);
        item.ifPresent(itemRepository::delete);
        return ResponseEntity.ok().body(new Error("File delete success, file: " + filename));
    }
}
