package ru.netology.cloudservice.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class Error {
    private static int serial = 0;
    private int id;
    private String timestamp;
    @NotNull
    private String message;

    public Error(String message) {
        this(message, 0);
    }

    private Error(String message, int id) {
        this.id = id == 0 ? ++serial : id;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.message = message;
    }
}
