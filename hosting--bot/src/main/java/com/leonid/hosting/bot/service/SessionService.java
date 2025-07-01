package com.leonid.hosting.bot.service;

import com.leonid.hosting.bot.model.SessionStatus;
import com.leonid.hosting.bot.model.UserSession;
import com.leonid.model.ApplicationStack;
import com.leonid.model.ApplicationStatus;
import com.leonid.model.DeploymentDto;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionService {

    private static final int THRESHOLD_SECONDS = 30;

    private final Map<Long, UserSession> sessions = new ConcurrentHashMap<>();

    public UserSession create(Long chatId) {
        UserSession session = UserSession.builder()
                .status(SessionStatus.INITIALIZED)
                .createdAt(LocalDateTime.now())
                .lastDeploymentsCheck(LocalDateTime.MIN)
                .build();

        sessions.put(chatId, session);
        return session;
    }

    public UserSession getSession(Long chatId) {
        return sessions.computeIfAbsent(chatId, this::create);
    }

    public void updateStack(Long chatId, ApplicationStack stack) {
        sessions.computeIfPresent(chatId, (id, session) -> {
            session.setStack(stack);
            session.setCreatedAt(LocalDateTime.now());
            return session;
        });
    }

    public void updateAppName(Long chatId, String appName) {
        sessions.computeIfPresent(chatId, (id, session) -> {
            session.setAppName(appName);
            session.setCreatedAt(LocalDateTime.now());
            return session;
        });
    }

    public boolean updateAppStatus(Long chatId, String instanceId, ApplicationStatus status) {
        UserSession session = sessions.get(chatId);
        if (session == null) {
            return false;
        }

        List<DeploymentDto> deployments = session.getCachedDeployments();
        if (deployments == null) {
            return false;
        }

        for (DeploymentDto deployment : deployments) {
            if (deployment.getInstanceId().equals(instanceId)) {
                deployment.setStatus(status);
                return true;
            }
        }
        return false;
    }



    public void clearSession(Long chatId) {
        sessions.remove(chatId);
    }

    public void updateLastInstanceInvocation(Long chatId) {
        sessions.computeIfPresent(chatId, (id, session) -> {
            session.setLastDeploymentsCheck(LocalDateTime.now());
            return session;
        });
    }

    public boolean shouldCallDeployments(Long chatId) {
        UserSession session = getSession(chatId);
        return Duration.between(session.getLastDeploymentsCheck(), LocalDateTime.now()).getSeconds() > THRESHOLD_SECONDS;
    }

    public void updateCachedDeployments(Long chatId, List<DeploymentDto> deployments) {
        sessions.computeIfPresent(chatId, (id, session) -> {
            session.setCachedDeployments(deployments);
            session.setLastDeploymentsCheck(LocalDateTime.now());
            return session;
        });
    }
}

