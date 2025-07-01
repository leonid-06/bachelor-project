package com.leonid.hosting.bot.service.commads;

import com.leonid.hosting.bot.model.BotRequest;
import com.leonid.hosting.bot.model.BotAction;
import com.leonid.hosting.bot.model.CommandName;
import com.leonid.hosting.bot.model.UserSession;
import com.leonid.hosting.bot.service.MessageService;
import com.leonid.hosting.bot.service.SessionService;
import com.leonid.hosting.bot.service.WorkerClientService;
import com.leonid.model.DeploymentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommandAction implements BotAction {

    private final SessionService sessionService;
    private final MessageService messageService;
    private final WorkerClientService workerService;

    private static final String WAITING_TEXT = "Talking to the database elves… 🧙‍♂️";

    @Override
    public void execute(BotRequest request) {
        String command = request.getMessage();
        Long chatId = request.getChatId();

        switch (CommandName.value(command)) {
            case START -> {
                sessionService.create(chatId);
                messageService.sendHomePage(chatId);
            }
            case DEPLOY -> {
                messageService.sendDeployHomePage(chatId);
            }
            case MY_APPS -> {
                try {
                    List<DeploymentDto> deployments;

                    if (sessionService.shouldCallDeployments(chatId)) {
                        messageService.sendMessage(chatId, WAITING_TEXT);
                        deployments = workerService.getDeployments(chatId);
                        sessionService.updateCachedDeployments(chatId, deployments);

                    } else {
                        deployments = sessionService.getSession(chatId).getCachedDeployments();
                        if (deployments == null) {
                            deployments = new ArrayList<>();
                        }
                    }
                    messageService.sendUserDeployments(chatId, deployments);

                } catch (Exception e) {
                    log.error("Failed to get deployments for chat {}", chatId, e);
                    messageService.sendMessage(chatId, "fetching deployments failed");
                }

            }
        }
    }
}
