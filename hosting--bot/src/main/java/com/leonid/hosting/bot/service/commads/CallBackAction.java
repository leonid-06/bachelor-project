package com.leonid.hosting.bot.service.commads;

import com.leonid.hosting.bot.model.BotRequest;
import com.leonid.hosting.bot.model.BotAction;
import com.leonid.hosting.bot.model.SessionStatus;
import com.leonid.hosting.bot.model.UserSession;
import com.leonid.hosting.bot.service.MessageService;
import com.leonid.hosting.bot.service.SessionService;
import com.leonid.hosting.bot.service.WorkerClientService;
import com.leonid.model.ApplicationStack;
import com.leonid.model.ApplicationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CallBackAction implements BotAction {

    private final SessionService sessionService;
    private final MessageService messageService;
    private final WorkerClientService workerService;

    @Override
    public void execute(BotRequest botRequest) {
        String data = botRequest.getMessage();
        Long chatId = botRequest.getChatId();

        UserSession session = sessionService.getSession(chatId);

        if (data.startsWith("DEPLOYMENT_MANAGE")){
            String raw = data.substring("DEPLOYMENT_MANAGE|".length());
            Map<String, String> params = Arrays.stream(raw.split(";"))
                    .map(s -> s.split("=", 2))
                    .collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));

            String instanceId = params.get("instanceId");
            ApplicationStatus status = ApplicationStatus.valueOf(params.get("status"));

            System.out.println("Status: " + status);

            messageService.sendDeploymentManagePanel(chatId, status, instanceId);
        } else if (data.startsWith("CHANGE_ST|")) {
            String raw = data.substring("CHANGE_ST|".length());
            Map<String, String> params = Arrays.stream(raw.split(";"))
                    .map(s -> s.split("=", 2))
                    .collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));

            String instanceId = params.get("instanceId");
            ApplicationStatus action = ApplicationStatus.valueOf(params.get("action"));

            boolean isUpdated = sessionService.updateAppStatus(chatId, instanceId, action);
            if (isUpdated) {
                System.out.println("Remote status change: " + workerService.updateAppStatus(instanceId, action));
            }

        } else {
            ApplicationStack stack = ApplicationStack.fromString(data);
            session.setStack(stack);
            session.setStatus(SessionStatus.WAIT_APP_NAME);
            messageService.sendMessage(chatId, "Alright, a " + stack + ". How are we going to call it? " +
                    "Please choose a name for your app");
        }

    }
}
