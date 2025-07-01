package com.leonid.hosting.bot.controller;

import com.leonid.hosting.bot.model.BotAction;
import com.leonid.hosting.bot.model.BotRequest;
import com.leonid.hosting.bot.model.BotRequestType;
import com.leonid.hosting.bot.service.commads.CallBackAction;
import com.leonid.hosting.bot.service.commads.CommandAction;
import com.leonid.hosting.bot.service.commads.DocumentAction;
import com.leonid.hosting.bot.service.commads.TextAction;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

import static com.leonid.hosting.bot.model.BotRequestType.*;

@Component
@Getter
public class BotDispatcher implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final String botToken;
    private final LongPollingUpdateConsumer updatesConsumer = this;

    private final Map<BotRequestType, BotAction> actionMap;

    @Autowired
    public BotDispatcher(@Value("${bot.token}") String botToken,
                         TextAction textAction, CommandAction commandAction,
                         CallBackAction callBackAction, DocumentAction documentAction) {
        this.botToken = botToken;
        this.actionMap = Map.of(
                CALLBACK, callBackAction,
                DOCUMENT, documentAction,
                TEXT, textAction,
                COMMAND, commandAction
        );
    }

    @SneakyThrows
    @Override
    public void consume(Update update) {
        BotRequest request = new BotRequest(update);
        actionMap.getOrDefault(request.getType(), null).execute(request);
    }
}



