package ru.netology.cloudservice.container;

import org.testcontainers.containers.PostgreSQLContainer;

public class CloudPostgresContainer extends PostgreSQLContainer<CloudPostgresContainer> {
    private static final String IMAGE_VERSION = "postgres:latest";
    private static CloudPostgresContainer container;

    private CloudPostgresContainer() {
        super(IMAGE_VERSION);
    }

    public static CloudPostgresContainer getInstance() {
        if (container == null) {
            container = new CloudPostgresContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("DB_URL", container.getJdbcUrl());
        System.setProperty("DB_USERNAME", container.getUsername());
        System.setProperty("DB_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
        super.stop();
    }
}
