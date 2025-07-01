package com.leonid.hosting.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@SpringBootApplication
public class HostingBotApplication {

    @Bean
    public TelegramClient telegramClient(@Value("${bot.token}") String token) {
        return new OkHttpTelegramClient(token);
    }

    public static void main(String[] args) {
        SpringApplication.run(HostingBotApplication.class, args);
    }
}


