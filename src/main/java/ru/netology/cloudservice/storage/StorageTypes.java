package ru.netology.cloudservice.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StorageTypes {
    FILESYSTEM("filesystem"),
    DATABASE("database");
    private final String type;
}
