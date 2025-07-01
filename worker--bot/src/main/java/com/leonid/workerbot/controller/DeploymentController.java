package com.leonid.workerbot.controller;

import com.leonid.model.DeployRequest;
import com.leonid.model.DeployResponse;
import com.leonid.model.DeploymentDto;
import com.leonid.workerbot.model.UpdateStatusRequest;
import com.leonid.workerbot.service.DeploymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DeploymentController {

    private final DeploymentService service;

    // todo cbd
    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/api/deploy")
    public ResponseEntity<String> deploy(@RequestBody DeployRequest request) {
        service.deploy(request);

//        try {
//            Thread.sleep(1000 * 120);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//
//        DeployResponse okResponse = DeployResponse.builder()
//                .appName(request.getAppName())
//                .chatId(request.getChatId())
//                .ipV4address("18.159.131.96")
//                .build();
//        restTemplate.postForLocation("http://localhost:8080/api/notify/success", okResponse);
//
        return ResponseEntity.ok("Request accepted");
    }

    @GetMapping("/api/deployments")
    public List<DeploymentDto> getDeployments(@RequestParam("chatId") Long chatId) {
        return service.getDeploymentsByChatId(chatId);
    }


    @PostMapping("/api/deployments/update-status")
    public ResponseEntity<Boolean> updateStatus(@RequestBody UpdateStatusRequest request) {
        boolean updated = service.updateApplicationStatus(request.getInstanceId(), request.getStatus());
        return ResponseEntity.ok(updated);
    }

}
