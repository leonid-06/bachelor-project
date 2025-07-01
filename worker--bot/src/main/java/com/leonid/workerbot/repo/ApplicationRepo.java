package com.leonid.workerbot.repo;

import com.leonid.workerbot.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepo extends JpaRepository<Application, String> {
    boolean existsByUserChatIdAndAppName(Long userId, String name);

    List<Application> findByInstanceId(String instanceId);

    List<Application> findByUser_ChatId(Long chatId);

}
