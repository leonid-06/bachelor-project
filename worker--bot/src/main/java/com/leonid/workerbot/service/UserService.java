package com.leonid.workerbot.service;

import com.leonid.workerbot.model.User;
import com.leonid.workerbot.repo.UserRepo;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo repo;

    public User findByChatID(Long chatID) {
        return repo.findById(chatID).orElseThrow(EntityNotFoundException::new);
    }
}
