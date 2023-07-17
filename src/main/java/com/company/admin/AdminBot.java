package com.company.admin;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class AdminBot extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            boolean isAdmin = message.getFrom().getUserName().equals("davlataliev");
            if (message.hasText() && message.getText().equals("/start") && isAdmin) {
                extracted(chatId,"Welcome admin");
            }
        }
    }

    private void extracted(Long chatId,String text){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "https://t.me/Bigdghbot";
    }

    @Override
    public String getBotToken() {
        return "5261432219:AAF46E6Ldz-p0aR6zqT3iHXZiinoNtN9A9g";
    }
}
