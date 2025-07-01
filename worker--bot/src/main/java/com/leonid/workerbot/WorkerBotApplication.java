package com.leonid.workerbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class WorkerBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorkerBotApplication.class, args);
    }
}
