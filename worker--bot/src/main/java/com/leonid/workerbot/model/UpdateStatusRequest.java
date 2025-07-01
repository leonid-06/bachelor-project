package com.leonid.workerbot.model;

import com.leonid.model.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStatusRequest {
    private String instanceId;
    private ApplicationStatus status;
}

