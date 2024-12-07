package ru.netology.cloudservice.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;
import ru.netology.cloudservice.CloudApp;
import ru.netology.cloudservice.container.CloudAppInitializer;
import ru.netology.cloudservice.container.ContainersEnvironment;
import ru.netology.cloudservice.model.Filename;
import ru.netology.cloudservice.model.Item;
import ru.netology.cloudservice.model.User;
import ru.netology.cloudservice.service.AuthService;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * This TEST is written by using @Testcontainers for Postgres INTEGRATION tests
 */
@TestPropertySource(locations = "classpath:application-tc.properties")
@SpringBootTest(classes = CloudApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("pgsql-test-container")
@ContextConfiguration(initializers = {CloudAppInitializer.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@RequiredArgsConstructor
class FileControllerIntegrationTest extends ContainersEnvironment {

    @Autowired
    private FileController fileController;
    @Autowired
    private AuthService authService;

    private final String filename = "sample_file.json";
    private String authToken;

    @BeforeEach
    void setUp() {
        authToken = authService.login(new User.Credentials("m@m.ru", "pwd")).getAuthToken();
        System.out.println("FileControllerIntegrationTest.setUp() => authService.login() : authToken = " + authToken);
    }

    @Test
    @Order(1)
    @DisplayName("saveItem() then FileController.saveItem()")
    void saveItem() {
        MultipartFile file = new MockMultipartFile("file", filename, "text/plain", filename.getBytes());
        try {
            FileUtils.deleteDirectory(new File("./src/main/resources/static"));
        } catch (IOException e) {
            System.out.println("FileControllerIntegrationTest.saveItem() : purge FileSystem");
        }
        ResponseEntity<?> responseEntity = fileController.saveItem(filename, file, Optional.of(authToken));
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }

    @Test
    @Order(2)
    @DisplayName("getItemList() then FileController.getItemList()")
    void getItemList() {
        Integer limit = 10;
        ResponseEntity<List<Item.FileDto>> responseEntity = fileController.getItemList(Optional.of(limit), Optional.of(authToken));
        Assertions.assertEquals(Objects.requireNonNull(responseEntity.getBody()).size(), 1); // due @Order(1) has added only ONE file to Cloud
    }

    @Test
    @Order(3)
    @DisplayName("downloadItem() then FileController.downloadItem()")
    void downloadItem() throws IOException {
        ResponseEntity<Object> responseEntity = fileController.downloadItem(filename, Optional.of(authToken));
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        InputStreamResource isr = (InputStreamResource) responseEntity.getBody();
        String fileContent = new String(Objects.requireNonNull(isr).getContentAsByteArray(), StandardCharsets.UTF_8);
        Assertions.assertEquals(filename, fileContent);
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }

    @Test
    @Order(4)
    @DisplayName("updateItem() then FileController.updateItem()")
    void updateItem() {
        Filename newFileName = new Filename(filename + ".renamed");
        ResponseEntity<?> responseEntity = fileController.updateItem(filename, newFileName, Optional.of(authToken));
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }

    @Test
    @Order(5)
    @DisplayName("deleteItem() then FileController.deleteItem()")
    void deleteItem() {
        ResponseEntity<?> responseEntity = fileController.deleteItem(filename + ".renamed", Optional.of(authToken));
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }
}
