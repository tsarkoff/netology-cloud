package ru.netology.cloudservice.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

public final class Logback {
    private static final Logger logger = LoggerFactory.getLogger(Logback.class);

    public static <T> ResponseEntity<T> log(ResponseEntity<T> action) {
        logger.info(String.valueOf(action));
        return action;
    }

    public static String log(String action) {
        logger.info(String.valueOf(action));
        return action;
    }
}
