package com.leonid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeployRequest {
    private ApplicationStack stack;
    private String fileId;
    private Long chatId;
    private String appName;
}
