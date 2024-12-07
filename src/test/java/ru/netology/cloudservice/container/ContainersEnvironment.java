package ru.netology.cloudservice.container;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class ContainersEnvironment {
    @Container
    public static PostgreSQLContainer<CloudPostgresContainer> postgreSQLContainer = CloudPostgresContainer.getInstance();
}
