package ru.netology.cloudservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudservice.exception.CloudExceptionHandlerAdvice;
import ru.netology.cloudservice.exception.FileNotFoundInDatabaseException;
import ru.netology.cloudservice.model.Item;
import ru.netology.cloudservice.model.ResultMessageDto;
import ru.netology.cloudservice.repository.ItemRepository;
import ru.netology.cloudservice.storage.Storage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@CloudExceptionHandlerAdvice
public class FileServiceImpl implements FileService {
    private final Storage storage;
    private final AuthService authService;
    private final ItemRepository itemRepository;

    @Override
    public List<Item.FileDto> getItemList(Optional<Integer> limit, Optional<String> token) {
        authService.validateToken(token);
        List<Item> items = itemRepository.findItemByOrderByFileSizeAsc(limit.map(Limit::of).orElseGet(Limit::unlimited));
        return items.stream().map(Item::getFile).toList();
    }

    @Override
    public Object downloadItem(String filename, Optional<String> token) {
        authService.validateToken(token);
        if (itemRepository.findItemByFileFilename(filename).isEmpty())
            throw new FileNotFoundInDatabaseException(Ops.FILE_DOWNLOAD, filename);
        return storage.read(filename);
    }

    @Override
    public ResultMessageDto saveItem(String filename, MultipartFile file, Optional<String> token) {
        authService.validateToken(token);
        storage.write(file);
        Item.FileDto itemFile = Item.FileDto.builder().filename(file.getOriginalFilename()).size(file.getSize()).build();
        Item item = Item.builder().file(itemFile).hash(itemFile.hashCode()).build();
        itemRepository.save(item);
        return new ResultMessageDto("File upload success, file: " + filename);
    }

    @Override
    public ResultMessageDto updateItem(String oldFilename, String newFilename, Optional<String> token) {
        authService.validateToken(token);
        Optional<Item> item = itemRepository.findItemByFileFilename(oldFilename);
        if (item.isEmpty())
            throw new FileNotFoundInDatabaseException(Ops.FILE_UPDATE, oldFilename);
        storage.rename(oldFilename, newFilename);
        item.get().getFile().setFilename(newFilename);
        itemRepository.save(item.get());
        return new ResultMessageDto("File update success, new filename: " + newFilename);
    }

    @Override
    public ResultMessageDto deleteItem(String filename, Optional<String> token) {
        authService.validateToken(token);
        Optional<Item> item = itemRepository.findItemByFileFilename(filename);
        if (item.isEmpty())
            throw new FileNotFoundInDatabaseException(Ops.FILE_DELETE, filename);
        storage.delete(filename);
        item.ifPresent(itemRepository::delete);
        return new ResultMessageDto("File delete success, file: " + filename);
    }
}
