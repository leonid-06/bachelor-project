package com.leonid.hosting.bot.controller;

import com.leonid.hosting.bot.service.MessageService;
import com.leonid.model.DeployResponse;
import com.leonid.model.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WorkerWebhookController {

    private final MessageService messageService;

    @PostMapping("/api/notify/success")
    public void handleSuccess(@RequestBody DeployResponse response) {
        messageService.sendDeployResult(response);
    }

    @PostMapping("/api/notify/failure")
    public void handleBad(@RequestBody ErrorResponse response) {
        messageService.sendDeployResult(response);
    }
}
