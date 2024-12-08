package ru.netology.cloudservice.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudservice.exception.FileInternalServerException;
import ru.netology.cloudservice.model.Item;
import ru.netology.cloudservice.repository.ItemRepository;
import ru.netology.cloudservice.service.Ops;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StorageDataBase implements Storage {

    private final ItemRepository itemRepository;

    // Download (GET /file)
    @Override
    public Object read(String filename, String owner) {
        Optional<Item> item = itemRepository.findItemByOwnerAndFileFilename(owner, filename);
        return item.get().getData();
    }

    // Upload (POST /file)
    @Override
    public void write(MultipartFile file, String owner) {
        // This could be done as Item object storing by itemRepository.save(item)
        // but just for case - here it is implemented via "JPA native @Query"
        try {
            int rows = itemRepository.saveItemToDatabase(
                    file.getOriginalFilename(),
                    file.getSize(),
                    file.hashCode(),
                    owner,
                    file.getInputStream().readAllBytes());
            if (rows <= 0) {
                throw new FileInternalServerException(Ops.FILE_UPLOAD, file.getOriginalFilename());
            }
        } catch (IOException e) {
            throw new FileInternalServerException(Ops.FILE_UPLOAD, file.getOriginalFilename());
        }
    }

    // Update (PUT /file)
    @Override
    public void rename(String oldFilename, String newFilename, String owner) {
        // already applied in FileService.updateItem() by itemRepository.save(item.get());
    }

    // Delete (DELETE /file)
    @Override
    public void delete(String filename, String owner) {
        // already applied in FileService.deleteItem() by item.ifPresent(itemRepository::delete);
    }
}
