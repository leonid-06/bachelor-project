package com.leonid.hosting.bot.service.commads;

import com.leonid.hosting.bot.model.BotRequest;
import com.leonid.hosting.bot.model.BotAction;
import com.leonid.hosting.bot.model.SessionStatus;
import com.leonid.hosting.bot.model.UserSession;
import com.leonid.hosting.bot.service.MessageService;
import com.leonid.hosting.bot.service.SessionService;
import com.leonid.model.ApplicationStack;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TextAction implements BotAction {

    private final SessionService sessionService;
    private final MessageService messageService;

    @Value("${app.name.regexp}")
    private String appNameRegexp;

    @Override
    public void execute(BotRequest botRequest) {
        Long chatId = botRequest.getChatId();
        String text = botRequest.getMessage();
        UserSession session = sessionService.getSession(chatId);

        if (session.getStatus().equals(SessionStatus.WAIT_APP_NAME)){

            if (text.matches(appNameRegexp)) {
                messageService.sendMessage(
                        chatId,
                        ApplicationStack.getArchiveDescription(session.getStack()),
                        true);
                session.setStatus(SessionStatus.WAIT_APP_FILE);
                session.setAppName(text);
            } else {
                messageService.sendMessage(chatId, "See FAQ");
            }

        } else {
            messageService.sendUnrecognized(chatId);
        }
    }
}
