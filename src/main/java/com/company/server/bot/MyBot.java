package com.company.server.bot;

import com.company.server.service.UserService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class MyBot extends TelegramLongPollingBot {

    public MyBot() {
        super("6384156412:AAFfuQyvX422k63RrMBg1-FrI9R2ZJVTrDk");
    }
    private final static UserService userService = new UserService();
    @Override
    public void onUpdateReceived(Update update) {

    }

    @Override
    public String getBotUsername() {
        return "t.me/uzfastfooudbot";
    }
}

/*
if (update.hasMessage()) {
            Message message = update.getMessage();
            Long chatId = 0L;

            if (update.hasMessage()) {
                chatId = update.getMessage().getChatId();
            } else if (update.hasCallbackQuery()) {
                chatId = update.getCallbackQuery().getMessage().getChatId();
            }

            Optional<User> optionalUser = userService.getById(chatId);
            String username = update.getMessage().getChat().getUserName();

            User user = optionalUser.orElse(User.builder()
                    .chatId(chatId)
                    .username(username)
                    .build());


            if (optionalUser.isEmpty()) {
                userService.add(user);
            }

            if (message.hasText()) {

            }
        }
 */