package ru.netology.cloudservice.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudservice.model.Item;
import ru.netology.cloudservice.model.ResultMessageDto;

import java.util.List;
import java.util.Optional;

@Service
public interface FileService {
    List<Item.FileDto> getItemList(Optional<Integer> limit, Optional<String> token);
    ResultMessageDto saveItem(String itemName, MultipartFile document, Optional<String> token);
    ResultMessageDto updateItem(String oldFilename, String newFilename, Optional<String> token);
    ResultMessageDto deleteItem(String filename, Optional<String> token);
    Object downloadItem(String filename, Optional<String> token);
}
