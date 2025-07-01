package com.leonid.hosting.bot.model;

import com.leonid.model.ApplicationStack;
import com.leonid.model.DeploymentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSession {
    private ApplicationStack stack;
    private String appName;
    private LocalDateTime createdAt;
    private SessionStatus status;
    private LocalDateTime lastDeploymentsCheck;
    private List<DeploymentDto> cachedDeployments = new ArrayList<>();

    public boolean isCompleted() {
        return stack != null && appName != null;
    }
}
