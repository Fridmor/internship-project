package com.github.fridmor;

import com.github.fridmor.telegram.Bot;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j(topic = "logback")
public class BotApp {
    private static final String BOT_NAME = System.getenv("BOT_NAME");
    private static final String BOT_TOKEN = System.getenv("BOT_TOKEN");

    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Bot(BOT_NAME, BOT_TOKEN));
        } catch (TelegramApiException e) {
            log.debug("debug log from {}: {}", BotApp.class.getSimpleName(), e.getMessage());
        }
    }
}