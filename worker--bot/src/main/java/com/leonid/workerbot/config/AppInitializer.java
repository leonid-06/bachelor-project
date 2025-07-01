package com.leonid.workerbot.config;

import com.leonid.model.ApplicationStack;
import com.leonid.workerbot.model.Instance;
import com.leonid.workerbot.model.InstanceStatus;
import com.leonid.workerbot.repo.InstanceRepo;
import com.leonid.workerbot.service.AsyncService;
import com.leonid.workerbot.service.EC2Service;
import com.leonid.workerbot.service.InstanceManageService;
import com.leonid.workerbot.service.S3Service;
import com.leonid.workerbot.service.ssm.SsmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppInitializer implements CommandLineRunner {
    private final AsyncService asyncService;

    @Value("${aws.ec2.pool.size}")
    private int instanceCount;

    @Override
    public void run(String... args) {
//        ApplicationStack.getEC2Hosted().forEach(stack -> {
//            for (int i = 0; i < instanceCount; i++) {
//                asyncService.creteInstance(stack);
//            }
//        });
    }
}

