package com.leonid.workerbot.service;

import com.leonid.model.*;
import com.leonid.workerbot.model.Application;
import com.leonid.workerbot.repo.ApplicationRepo;
import com.leonid.workerbot.service.ssm.SsmService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeploymentService {

    private final FileProcessingService fileProcessingService;
    private final ApplicationService applicationService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final SsmService ssmService;
    private final EC2Service ec2Service;
    private final ApplicationRepo applicationRepo;

    @Value("${gateway.origin}")
    private String gateWayOrigin;

    @Value("${gateway.urls.success}")
    private String successUrl;

    @Value("${gateway.urls.failure}")
    private String failureUrl;

    @Async
    public void deploy(DeployRequest request) {

        System.out.println("Starting of deploy process");

        // checking existing name in DB
        if (applicationService.existsByUserChatIdAndName(request.getChatId(), request.getAppName())) {
            ErrorResponse response = new ErrorResponse(request.getChatId(), "such app-name already exist");
            String url = gateWayOrigin + "/api/notify/failure";
            restTemplate.postForLocation(url, response);
            return;
        }


        String urlToDownload = fileProcessingService.resolveSrcFilePath(request);
        String instanceId = ec2Service.getAvailableInstance();
        ec2Service.sendRequestToStartInstance(instanceId);

        String ip = "134";



        ssmService
                .waitForSsm(instanceId, Duration.ofMinutes(2))
                .whenComplete((ssm, throwable) -> {
                    if (throwable != null) {
                        ErrorResponse response = new ErrorResponse(request.getChatId(), "ssm not working");
                        restTemplate.postForLocation(gateWayOrigin + failureUrl, response);
                        return;
                    }
                    String wgetCommand = String.format("wget '%s' -O /home/ubuntu/app.zip 2>/dev/null", urlToDownload);
                    List<String> commands = new ArrayList<>();
                    commands.add(wgetCommand);
                    commands.addAll(TelegramUtil.readScript(request.getStack()));

                    boolean successful = ssmService.send(commands, instanceId);
                    System.out.println("Commands was executed? " + (successful ? "yes" : "no"));

                    if (!successful) {
                        ErrorResponse response = new ErrorResponse(request.getChatId(), "ssm not working");
                        restTemplate.postForLocation(gateWayOrigin + failureUrl, response);
                        return;
                    }
                    DeployResponse okResponse = DeployResponse.builder()
                            .appName(request.getAppName())
                            .chatId(request.getChatId())
                            .ipV4address(ip)
                            .build();
                    restTemplate.postForLocation(gateWayOrigin + successUrl, okResponse);
                });
    }

    public List<DeploymentDto> getDeploymentsByChatId(Long chatId) {
        return applicationRepo.findByUser_ChatId(chatId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private DeploymentDto toDto(Application application) {
        return DeploymentDto.builder()
                .name(application.getAppName())
                .status(application.getStatus())
                .stack(application.getStack())
                .instanceId(application.getInstanceId())
                .ipOrUrl(application.getIpOrUrl())
                .build();
    }


    public boolean updateApplicationStatus(String instanceId, ApplicationStatus status) {
        return applicationRepo.findById(instanceId)
                .map(app -> {
                    app.setStatus(status);
                    applicationRepo.save(app);
                    return true;
                })
                .orElse(false);
    }
}


