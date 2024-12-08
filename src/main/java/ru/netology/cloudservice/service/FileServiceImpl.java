package ru.netology.cloudservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudservice.config.AppProps;
import ru.netology.cloudservice.exception.CloudExceptionHandlerAdvice;
import ru.netology.cloudservice.exception.FileNotFoundInDatabaseException;
import ru.netology.cloudservice.model.Item;
import ru.netology.cloudservice.model.ResultMessageDto;
import ru.netology.cloudservice.repository.ItemRepository;
import ru.netology.cloudservice.storage.Storage;
import ru.netology.cloudservice.storage.StorageDataBase;
import ru.netology.cloudservice.storage.StorageFileSystem;
import ru.netology.cloudservice.storage.StorageTypes;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@CloudExceptionHandlerAdvice
public class FileServiceImpl implements FileService {
    private final AppProps props;
    private final AuthService authService;
    private final ItemRepository itemRepository;

    @Bean
    private Storage storage() {
        return switch (props.getStorageType()) {
            case FILESYSTEM -> new StorageFileSystem(props);
            case DATABASE -> new StorageDataBase(itemRepository);
        };
    }

    @Override
    public List<Item.FileDto> getItemList(Optional<Integer> limit, Optional<String> token) {
        authService.validateToken(token);
        String owner = authService.getUsernameByToken(token);
        List<Item> items = itemRepository.findItemByOwnerOrderByFileSizeAsc(owner, limit.map(Limit::of).orElseGet(Limit::unlimited));
        return items.stream().map(Item::getFile).toList();
    }

    @Override
    public Object downloadItem(String filename, Optional<String> token) {
        authService.validateToken(token);
        String owner = authService.getUsernameByToken(token);
        if (itemRepository.findItemByOwnerAndFileFilename(owner, filename).isEmpty())
            throw new FileNotFoundInDatabaseException(Ops.FILE_DOWNLOAD, filename);
        return storage().read(filename, owner);
    }

    @Override
    public ResultMessageDto saveItem(String filename, MultipartFile file, Optional<String> token) {
        authService.validateToken(token);
        String owner = authService.getUsernameByToken(token);
        storage().write(file, owner);
        if (props.getStorageType().equals(StorageTypes.FILESYSTEM)) {
            Item.FileDto itemFile = Item.FileDto.builder().filename(file.getOriginalFilename()).size(file.getSize()).build();
            Item item = Item.builder().file(itemFile).hash(itemFile.hashCode()).owner(owner).build();
            itemRepository.save(item);
        }
        return new ResultMessageDto("File upload success, file: " + filename);
    }

    @Override
    public ResultMessageDto updateItem(String oldFilename, String newFilename, Optional<String> token) {
        authService.validateToken(token);
        String owner = authService.getUsernameByToken(token);
        Optional<Item> item = itemRepository.findItemByOwnerAndFileFilename(owner, oldFilename);
        if (item.isEmpty())
            throw new FileNotFoundInDatabaseException(Ops.FILE_UPDATE, oldFilename);
        if (props.getStorageType().equals(StorageTypes.DATABASE))
            storage().rename(oldFilename, newFilename, owner);
        item.get().getFile().setFilename(newFilename);
        itemRepository.save(item.get());
        return new ResultMessageDto("File update success, new filename: " + newFilename);
    }

    @Override
    public ResultMessageDto deleteItem(String filename, Optional<String> token) {
        authService.validateToken(token);
        String owner = authService.getUsernameByToken(token);
        Optional<Item> item = itemRepository.findItemByOwnerAndFileFilename(owner, filename);
        if (item.isEmpty())
            throw new FileNotFoundInDatabaseException(Ops.FILE_DELETE, filename);
        if (props.getStorageType().equals(StorageTypes.DATABASE))
            storage().delete(filename, owner);
        item.ifPresent(itemRepository::delete);
        return new ResultMessageDto("File delete success, file: " + filename);
    }
}
