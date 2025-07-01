package com.leonid.hosting.bot.service;

import com.leonid.model.ApplicationStatus;
import com.leonid.model.DeployRequest;
import com.leonid.model.DeploymentDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WorkerClientService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${worker.origin}")
    private String workerOrigin;

    public void send(DeployRequest request) {
        String url = workerOrigin + "/api/deploy";
        restTemplate.postForLocation(url, request);
    }

    public List<DeploymentDto> getDeployments(Long chatId) {
        System.out.println("Start of getDeployments");
        String url = workerOrigin + "/api/deployments?chatId=" + chatId;
        System.out.println("Url is: " + url);

        ResponseEntity<List<DeploymentDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        System.out.println("End of getDeployments");

        return response.getBody();
    }

    public boolean updateAppStatus(String instanceId, ApplicationStatus status) {
        String url = workerOrigin + "/api/deployments/update-status";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body;
        body = new HashMap<>();
        body.put("instanceId", instanceId);
        body.put("status", status.name());

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Boolean> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    Boolean.class
            );

            return Boolean.TRUE.equals(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
