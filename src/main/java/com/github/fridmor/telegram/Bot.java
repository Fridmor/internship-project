package com.github.fridmor.telegram;

import com.github.fridmor.BotApp;
import com.github.fridmor.util.command.CommandExecutor;
import com.github.fridmor.util.command.CommandHandler;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

@Slf4j(topic = "logback")
@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot {
    private final String BOT_NAME;
    private final String BOT_TOKEN;

    private static final String GRAPH_COMMAND = "graph";

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
            try {
                if (update.hasMessage()) {
                    Message message = update.getMessage();
                    if (message.hasText() || message.hasLocation()) {
                        handleIncomingMessage(message);
                    }
                }
            } catch (Exception e) {
                log.debug("debug log from {}: {}", Bot.class.getSimpleName(), e.getMessage());
            }
    }

    private void handleIncomingMessage(Message message) throws TelegramApiException, PythonExecutionException, IOException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        try {
            CommandHandler commandHandler = new CommandHandler(message.getText());
            CommandExecutor commandExecutor = new CommandExecutor(commandHandler);
            if (message.getText().contains(GRAPH_COMMAND)) {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(message.getChatId().toString());
                sendPhoto.setPhoto(new InputFile(commandExecutor.commandExecuteWithGraphReturn()));
                execute(sendPhoto);
            } else {
                sendMessage.setText(commandExecutor.commandExecuteWithTextReturn());
                execute(sendMessage);
            }
        } catch (IllegalArgumentException e) {
            log.info("info log from {}: {}", BotApp.class.getSimpleName(), e.getMessage());
            sendMessage.setText(e.getMessage());
            execute(sendMessage);
        }

    }
}
