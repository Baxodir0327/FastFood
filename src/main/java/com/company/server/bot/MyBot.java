package com.company.server.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class MyBot extends TelegramLongPollingBot {
    @Override
    public void onUpdateReceived(Update update) {

    }

    @Override
    public String getBotUsername() {
        return "t.me/uzfastfooudbot";
    }

    @Override
    public String getBotToken() {
        return "6384156412:AAFfuQyvX422k63RrMBg1-FrI9R2ZJVTrDk";
    }
}
