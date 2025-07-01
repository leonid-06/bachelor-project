package com.leonid.workerbot.controller;

import com.leonid.workerbot.service.InstanceManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final InstanceManageService service;

    @GetMapping("/instances")
    public String instances() {
        return service.getInstances().toString();
    }
}
