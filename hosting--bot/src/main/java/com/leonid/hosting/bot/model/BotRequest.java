package com.leonid.hosting.bot.model;

import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;

@Data
public class BotRequest {
    private final Long chatId;
    private final BotRequestType type;
    private Document document;
    private Update update;
    private String message;
    private String username;

    public BotRequest(Update update) {
        this.update = update;

        if (update.hasCallbackQuery()) {
            this.chatId = update.getCallbackQuery().getFrom().getId();
            this.message = update.getCallbackQuery().getData();
            this.username = update.getCallbackQuery().getFrom().getUserName();
            this.type = update.getCallbackQuery().getData().startsWith("/")
                    ? BotRequestType.COMMAND
                    : BotRequestType.CALLBACK;

        } else if (update.hasMessage() && update.getMessage().isCommand()) {
            this.message = update.getMessage().getText();
            this.chatId = update.getMessage().getChatId();
            this.username = update.getMessage().getFrom().getUserName();
            this.type = BotRequestType.COMMAND;
        }
        else if (update.hasMessage() && update.getMessage().hasText()) {
            this.message = update.getMessage().getText();
            this.chatId = update.getMessage().getChatId();
            this.username = update.getMessage().getFrom().getUserName();
            this.type = BotRequestType.TEXT;
        } else if (update.hasMessage() && update.getMessage().hasDocument()) {
            this.chatId = update.getMessage().getChatId();
            this.username = update.getMessage().getFrom().getUserName();
            this.document = update.getMessage().getDocument();
            this.type = BotRequestType.DOCUMENT;
        } else {
            throw new RuntimeException("Update has no chat ID");
        }
    }
}

