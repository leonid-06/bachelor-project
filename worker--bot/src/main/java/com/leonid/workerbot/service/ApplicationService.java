package com.leonid.workerbot.service;

import com.leonid.workerbot.repo.ApplicationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final ApplicationRepo applicationRepo;

    public boolean existsByUserChatIdAndName(Long userId, String name) {
        return applicationRepo.existsByUserChatIdAndAppName(userId, name);
    }
}
