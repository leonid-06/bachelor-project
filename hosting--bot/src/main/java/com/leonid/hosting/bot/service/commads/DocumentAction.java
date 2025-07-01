package com.leonid.hosting.bot.service.commads;

import com.leonid.hosting.bot.model.BotRequest;
import com.leonid.hosting.bot.model.BotAction;
import com.leonid.hosting.bot.model.SessionStatus;
import com.leonid.hosting.bot.model.UserSession;
import com.leonid.hosting.bot.service.MessageService;
import com.leonid.hosting.bot.service.SessionService;
import com.leonid.hosting.bot.service.WorkerClientService;
import com.leonid.model.DeployRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Document;

@Service
@RequiredArgsConstructor
public class DocumentAction implements BotAction {

    private final SessionService sessionService;
    private final MessageService messageService;
    private final WorkerClientService workerClientService;

    @Override
    public void execute(BotRequest botRequest) {
        Long chatId = botRequest.getChatId();
        Document document = botRequest.getDocument();
        UserSession session = sessionService.getSession(chatId);

        if (session.isCompleted() && session.getStatus().equals(SessionStatus.WAIT_APP_FILE)){
            DeployRequest request = DeployRequest.builder()
                    .chatId(chatId)
                    .fileId(document.getFileId())
                    .appName(session.getAppName())
                    .stack(session.getStack())
                    .build();
            messageService.sendMessage(chatId, "Excellent, Wait a minute!");

            workerClientService.send(request);
            sessionService.clearSession(chatId);
//            messageService.sendMessage(chatId, "Excellent, Wait a minute!");
        } else {
            messageService.sendUnrecognized(chatId);
        }
    }
}
