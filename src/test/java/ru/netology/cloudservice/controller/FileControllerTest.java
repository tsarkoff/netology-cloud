package ru.netology.cloudservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.netology.cloudservice.config.AppProps;
import ru.netology.cloudservice.model.Filename;
import ru.netology.cloudservice.service.FileService;
import ru.netology.cloudservice.storage.IoTypes;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * This TEST is written by using @SpringBootTest with slow App Context initialization
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FileControllerTest {

    @Mock
    private FileService mockFileService;

    @Mock
    private AppProps mockProps;

    @InjectMocks
    private FileController fileController;
    private MockMvc mockMvc;
    private final String filename = "sample_file.json";
    private final String authToken = "lex34pou5p9834u5n3span394u58u09";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(fileController).build();
    }

    @Test
    @Order(1)
    @DisplayName("saveItem() then FileService.saveItem()")
    void saveItem() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", filename, "text/plain", filename.getBytes());
        MockMultipartHttpServletRequestBuilder multiPartBuilder = MockMvcRequestBuilders.multipart("/file");
        mockMvc.perform(multiPartBuilder
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .param("filename", filename)
                        .header("auth-token", authToken)
                )
                .andExpect(status().isOk())
                .andDo(print());
        Mockito.verify(mockFileService).saveItem(filename, file, Optional.of(authToken));
    }

    @Test
    @Order(2)
    @DisplayName("getItemList() then FileService.getItemList()")
    void getItemList() throws Exception {
        Integer limit = 10;
        mockMvc.perform(get("/list")
                        .param("limit", String.valueOf(limit))
                        .header("auth-token", authToken)
                )
                .andExpect(status().isOk())
                .andDo(print());
        Mockito.verify(mockFileService).getItemList(Optional.of(limit), Optional.of(authToken));
    }

    @Test
    @Order(3)
    @DisplayName("downloadItem() then FileService.downloadItem()")
    void downloadItem() throws Exception {
        Mockito.doReturn(IoTypes.MULTIPART).when(mockProps).getStorageIoType();
        mockMvc.perform(get("/file")
                        .param("filename", filename)
                        .header("auth-token", authToken)
                )
                .andExpect(status().isOk())
                .andDo(print());
        Mockito.verify(mockFileService).downloadItem(filename, Optional.of(authToken));
    }

    @Test
    @Order(4)
    @DisplayName("updateItem() then FileService.updateItem()")
    void updateItem() throws Exception {
        Filename newFileName = new Filename(filename + ".renamed");
        mockMvc.perform(put("/file")
                        .param("filename", filename)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(newFileName))
                        .header("auth-token", authToken)
                )
                .andExpect(status().isOk())
                .andDo(print());
        Mockito.verify(mockFileService).updateItem(filename, newFileName.getFilename(), Optional.of(authToken));
    }

    @Test
    @Order(5)
    @DisplayName("deleteItem() then FileService.deleteItem()")
    void deleteItem() throws Exception {
        mockMvc.perform(delete("/file")
                        .param("filename", filename)
                        .header("auth-token", authToken)
                )
                .andExpect(status().isOk())
                .andDo(print());
        Mockito.verify(mockFileService).deleteItem(filename, Optional.of(authToken));
    }
}