package com.leonid.workerbot.model;

import com.leonid.model.ApplicationStack;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.ec2.endpoints.internal.Value;

import static com.leonid.workerbot.model.InstanceStatus.PROVISIONING;

@Data
@Entity
@Table
@NoArgsConstructor
public class Instance {
    @Id
    private String instanceId;
    private String publicIp;
    @Enumerated(EnumType.STRING)
    private ApplicationStack stack;
    @Enumerated(EnumType.STRING)
    private InstanceStatus status;

    public Instance(String instanceId, ApplicationStack stack) {
        this.instanceId = instanceId;
        this.stack = stack;
        this.status = PROVISIONING;
    }
}

