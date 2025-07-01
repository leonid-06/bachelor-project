package com.leonid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeploymentDto {
    private String name;
    private ApplicationStack stack;
    private ApplicationStatus status;
    private int uptimeMinutes;
    private String ipOrUrl;
    private LocalDateTime createdAt;
    private String instanceId;
}
