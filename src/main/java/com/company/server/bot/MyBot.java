package com.company.server.bot;

import com.company.server.enums.States;
import com.company.server.model.MyUser;
import com.company.server.service.UserService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class MyBot extends TelegramLongPollingBot {
   private static UserService userService = new UserService();
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            Long chatId = 0L;
            if (update.hasMessage()) {
                chatId = update.getMessage().getChatId();
            } else if (update.hasCallbackQuery()) {
                chatId = update.getCallbackQuery().getMessage().getChatId();
            }

            Optional<MyUser> optionalUser = userService.getById(chatId);
            String firstname = update.getMessage().getChat().getFirstName();
            String username = update.getMessage().getChat().getUserName();
            String lastname = update.getMessage().getChat().getLastName();

            MyUser user = optionalUser.orElse(MyUser.builder()
                    .id(UUID.randomUUID())
                    .chatId(chatId)
                    .fullName(firstname + "" + lastname)
                    .username(username)
                    .states(States.MAIN_PAGE)
                    .build());


            if (optionalUser.isEmpty()) {
                userService.add(user);
            }

            if (message.hasText()) {

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
