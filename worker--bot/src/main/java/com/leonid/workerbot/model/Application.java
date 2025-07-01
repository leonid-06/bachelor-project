package com.leonid.workerbot.model;

import com.leonid.model.ApplicationStack;
import com.leonid.model.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Application {
    @Id
    private String instanceId;

    @ManyToOne
    private User user;
    private String appName;
    private String ipOrUrl;

    @Enumerated(EnumType.STRING)
    private ApplicationStack stack;
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;
}