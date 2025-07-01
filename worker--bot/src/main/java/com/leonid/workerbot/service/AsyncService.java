package com.leonid.workerbot.service;

import com.leonid.model.ApplicationStack;
import com.leonid.workerbot.model.Instance;
import com.leonid.workerbot.model.InstanceStatus;
import com.leonid.workerbot.service.ssm.SsmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.*;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class AsyncService {

    private final EC2Service ec2Service;
    private final InstanceManageService instanceManageService;
    private final SsmService ssmService;

    private String getScriptEntry(ApplicationStack stack) {
        String filePath = "init-scripts" + File.separator + stack.name().toLowerCase() + "-instance-setup.sh";
        try {
            return String.join("\n", readLines(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> readLines(String filePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(filePath);

        try (InputStream input = resource.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            return reader.lines().collect(Collectors.toList());
        }
    }


    @Async
    public void creteInstance(ApplicationStack stack) {
        log.info("Start process of create instance {} for", stack.name());
        var scriptText = getScriptEntry(stack);
        String instanceId = ec2Service.sendRequestToCreateInstance(scriptText, stack.name());
        Instance instance = instanceManageService.create(instanceId, stack);

        ssmService.waitForSetup(instanceId).whenComplete((instanceStatus, e) -> {

            if (e != null) {
                log.error("Error while setup script", e);
                return;
            }
            ssmService
                    .waitForSsm(instanceId, Duration.ofMinutes(2))
                    .whenComplete((ssm, eee) -> {
                        if (eee != null) {
                            log.error("Error while ssm", eee);
                            return;
                        }
                        ec2Service.stopInstance(instanceId);
                        instance.setStatus(InstanceStatus.IDLE);
                        instanceManageService.update(instance);
                    });

        });
    }
}
