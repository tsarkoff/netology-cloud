package ru.netology.cloudservice;

import lombok.Data;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Data
public class CloudApp implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(CloudApp.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run();
    }

    @Override
    public void run(String... args) {
        System.out.println("==> CloudApp started");
    }
}
