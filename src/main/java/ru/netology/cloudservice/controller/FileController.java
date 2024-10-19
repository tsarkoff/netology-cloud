package ru.netology.cloudservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudservice.exception.CloudExceptionHandlerAdvice;
import ru.netology.cloudservice.model.Error;
import ru.netology.cloudservice.model.Filename;
import ru.netology.cloudservice.service.FileService;

import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping
@RequiredArgsConstructor
@CloudExceptionHandlerAdvice // all exceptions  handled by ExceptionHandlerAdvice (might be several of Handlers)
public class FileController {
    private final FileService fileService;

    @GetMapping("/list")
    public ResponseEntity<?> getItemList(
            @RequestParam("limit") Optional<Integer> limit,
            @RequestHeader("auth-token") Optional<String> headerAuthToken) {
        return log(fileService.getItemList(limit, headerAuthToken));
    }

    @GetMapping("/file")
    public ResponseEntity<?> downloadItem(
            @RequestParam("filename") String filename,
            @RequestHeader("auth-token") Optional<String> headerAuthToken) {
        return log(fileService.downloadItem(filename, headerAuthToken));
    }

    @PostMapping(value = "/file"/*, produces = MediaType.MULTIPART_FORM_DATA_VALUE*/)
    public ResponseEntity<Error> saveItem(@RequestParam("filename") String filename,
                                          @RequestPart(name = "file") MultipartFile file,
                                          @RequestHeader("auth-token") Optional<String> headerAuthToken) {
        return log(fileService.saveItem(filename, file, headerAuthToken));
    }

    @PutMapping("/file")
    public ResponseEntity<Error> updateItem(
            @RequestParam("filename") String oldFilename,
            @Valid @RequestBody Filename newFilename,
            @RequestHeader("auth-token") Optional<String> headerAuthToken) {
        return log(fileService.updateItem(oldFilename, newFilename.getFilename(), headerAuthToken));
    }

    @DeleteMapping("/file")
    public ResponseEntity<Error> deleteItem(
            @RequestParam("filename") String filename,
            @RequestHeader("auth-token") Optional<String> headerAuthToken) {
        return log(fileService.deleteItem(filename, headerAuthToken));
    }

    public <T> ResponseEntity<T> log(ResponseEntity<T> action) {
        System.out.println(action);
        return action;
    }
}
