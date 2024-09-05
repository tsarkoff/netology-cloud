package ru.netology.cloudservice.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import ru.netology.cloudservice.storage.IoTypes;

@Configuration
@ConfigurationProperties(prefix = "cloud.app")
@RequiredArgsConstructor
@PropertySource("classpath:application.properties")
@Getter
public class AppProps {
    @Value("${cloud.app.storage.path}")
    private String storagePath;
    @Value("${cloud.app.storage.io-type}")
    private IoTypes storageIoType;
}
