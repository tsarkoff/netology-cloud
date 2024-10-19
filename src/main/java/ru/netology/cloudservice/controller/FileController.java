package ru.netology.cloudservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudservice.config.AppProps;
import ru.netology.cloudservice.exception.CloudExceptionHandlerAdvice;
import ru.netology.cloudservice.logging.Logback;
import ru.netology.cloudservice.model.Filename;
import ru.netology.cloudservice.model.Item;
import ru.netology.cloudservice.model.ResultMessageDto;
import ru.netology.cloudservice.service.FileService;
import ru.netology.cloudservice.storage.IoTypes;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping
@RequiredArgsConstructor
@CloudExceptionHandlerAdvice // all exceptions  handled by ExceptionHandlerAdvice (might be several of Handlers)
public class FileController {
    private final FileService fileService;
    private final AppProps props;

    @GetMapping("/list")
    public ResponseEntity<List<Item.FileDto>> getItemList(
            @RequestParam("limit") Optional<Integer> limit,
            @RequestHeader("auth-token") Optional<String> headerAuthToken) {
        List<Item.FileDto> files = fileService.getItemList(limit, headerAuthToken);
        return Logback.log(ResponseEntity.ok().body(files));
    }

    @GetMapping("/file")
    public ResponseEntity<Object> downloadItem(
            @RequestParam("filename") String filename,
            @RequestHeader("auth-token") Optional<String> headerAuthToken) {
        Object file = fileService.downloadItem(filename, headerAuthToken);
        ResponseEntity<Object> re = ResponseEntity.ok()
                .contentType(
                        props.getStorageIoType().equals(IoTypes.MULTIPART)
                                ? MediaType.MULTIPART_MIXED
                                : MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(filename).toString())
                .body(file);
        return Logback.log(re);
    }

    @PostMapping(value = "/file"/*, produces = MediaType.MULTIPART_FORM_DATA_VALUE*/)
    public ResponseEntity<ResultMessageDto> saveItem(@RequestParam("filename") String filename,
                                                     @RequestPart(name = "file") MultipartFile file,
                                                     @RequestHeader("auth-token") Optional<String> headerAuthToken) {
        ResultMessageDto resultMessageDto = fileService.saveItem(filename, file, headerAuthToken);
        return Logback.log(ResponseEntity.ok().body(resultMessageDto));
    }

    @PutMapping("/file")
    public ResponseEntity<ResultMessageDto> updateItem(
            @RequestParam("filename") String oldFilename,
            @Valid @RequestBody Filename newFilename,
            @RequestHeader("auth-token") Optional<String> headerAuthToken) {
        ResultMessageDto resultMessageDto = fileService.updateItem(oldFilename, newFilename.getFilename(), headerAuthToken);
        return Logback.log(ResponseEntity.ok().body(resultMessageDto));
    }

    @DeleteMapping("/file")
    public ResponseEntity<ResultMessageDto> deleteItem(
            @RequestParam("filename") String filename,
            @RequestHeader("auth-token") Optional<String> headerAuthToken) {
        ResultMessageDto resultMessageDto = fileService.deleteItem(filename, headerAuthToken);
        return Logback.log(ResponseEntity.ok().body(resultMessageDto));
    }
}
