package ru.netology.cloudservice.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Ops {
    LOGIN_FAILED("Wrong username or password"),
    TOKEN_HEADER_ABSENT("header 'auth-token' absent"),
    TOKEN_NOT_FOUND_IN_DB("auth token absent in User Authority DB"),
    FILE_UPLOAD("upload"),
    FILE_UPDATE("update"),
    FILE_DOWNLOAD("download"),
    FILE_DELETE("delete");
    private final String op;
}
