package com.company.server.bot;

import com.company.server.service.CreateButtonService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class MyBot extends TelegramLongPollingBot {
    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("started");
        if (update.hasMessage()) {
            String text = update.getMessage().getText();
            System.out.println(text);
            if (text.equals("/start")) {
                CreateButtonService service = new CreateButtonService();
                InlineKeyboardMarkup inlineKeyboard = service.createInlineKeyboard(List.of("1"),10);
                SendMessage sendMessage = new SendMessage();
                Long chatId = update.getMessage().getChatId();
                sendMessage.setChatId(chatId);
                sendMessage.setText("Tanlang");
                sendMessage.setReplyMarkup(inlineKeyboard);
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                ReplyKeyboardMarkup replyButton = service.createReplyButton(List.of("Saloom", "Ishlar qanday"));
                sendMessage.setReplyMarkup(replyButton);
                sendMessage.setChatId(chatId);
                sendMessage.setText("Salom");
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }

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
