package ru.netology.cloudservice.container;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

public class CloudAppInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        TestPropertyValues.of(
                "spring.datasource.url=" + postgreSQLContainer().getJdbcUrl(),
                "spring.datasource.username=" + postgreSQLContainer().getUsername(),
                "spring.datasource.password=" + postgreSQLContainer().getPassword()
        ).applyTo(configurableApplicationContext.getEnvironment());
    }

    public PostgreSQLContainer<CloudPostgresContainer> postgreSQLContainer() {
        return CloudPostgresContainer.getInstance();
    }
}
