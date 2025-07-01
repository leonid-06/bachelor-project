package com.leonid.workerbot.service;

import com.leonid.model.ApplicationStack;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

public class TelegramUtil {

//    public static String getDeployScriptPath(ApplicationStack stack) {
//        return "deploy-scripts/" + stack.name().toLowerCase() + "-instance-deploy.sh";
//    }


    public static List<String> readScript(ApplicationStack stack) {
        String path = "deploy-scripts/" + stack.name().toLowerCase() + "-instance-deploy.sh";
        ClassPathResource resource = new ClassPathResource(path);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            return reader.lines().collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read script: " + path, e);
        }
    }


}
