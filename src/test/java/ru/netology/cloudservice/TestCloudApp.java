package ru.netology.cloudservice;

import org.springframework.boot.SpringApplication;

public class TestCloudApp {

    public static void main(String[] args) {
        SpringApplication.from(CloudApp::main).with(TestcontainersConfiguration.class).run(args);
    }

}
