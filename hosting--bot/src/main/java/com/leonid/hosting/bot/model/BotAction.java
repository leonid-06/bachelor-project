package com.leonid.hosting.bot.model;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

public interface BotAction {
    void execute(BotRequest botRequest) throws TelegramApiException, IOException;
}
