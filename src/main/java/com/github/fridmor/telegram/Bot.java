package com.github.fridmor.telegram;

import com.github.fridmor.util.CommandHandler;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;

public class Bot extends TelegramLongPollingBot {

    private final String BOT_NAME;
    private final String BOT_TOKEN;

    public Bot(String botName, String botToken) {
        this.BOT_NAME = botName;
        this.BOT_TOKEN = botToken;
    }

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
            System.out.println("upsss");
        }
    }

    private void handleIncomingMessage(Message message) throws TelegramApiException, PythonExecutionException, IOException {
//        File file = CommandHandler.commandHandle(message.getText());
//        SendPhoto sendPhoto = new SendPhoto();
//        sendPhoto.setChatId(message.getChatId().toString());
//        sendPhoto.setPhoto(new InputFile(file));
//        sendPhoto.setCaption("Graph");


        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(message.getText());

        execute(sendMessage);
    }
}
