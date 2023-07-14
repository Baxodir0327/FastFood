package com.company.server.bot;

import com.company.server.enums.State;
import com.company.server.service.UserService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import com.company.server.model.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MyBot extends TelegramLongPollingBot {
        private CreateButtonService createButtonService = new CreateButtonService();
    private List<User> users = new ArrayList<>();

    public MyBot() {
        super("6384156412:AAFfuQyvX422k63RrMBg1-FrI9R2ZJVTrDk");
    }

    private final static UserService userService = new UserService();

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            User user = null;
            Optional<User> byId = userService.getById(chatId);

            user = byId.orElseGet(() -> users.stream().parallel().
                    filter(u -> u.getChatId().equals(chatId)).findFirst()
                    .orElse(null));



            if (message.hasText()) {
                String text = message.getText();
                if (user == null && text.equals("/start")) {
                    user = new User();
                    user.setState(State.ENTER_NAME);
                    user.setUsername(message.getFrom().getUserName());
                    user.setChatId(chatId);
                    getExecuteMessage("Enter full name", chatId);
                    users.add(user);
                } else if (user.getState().equals(State.ENTER_NAME)) {
                    user.setFullName(text);
                    user.setState(State.PHONE_NUMBER);

                    ReplyKeyboardMarkup shareContact = createButtonService.createReplyButton(List.of("Share contact"), true);
                    getExecuteMessage("Share phone number!", chatId, shareContact);

                } else if (user.getState().equals(State.MAIN_PAGE)) {
                    getExecuteMessage("main page", chatId);

                }
            }if (message.hasContact() && user.getState().equals(State.PHONE_NUMBER)){
                Contact contact = message.getContact();
                String phoneNumber = contact.getPhoneNumber();
                user.setPhoneNumber(phoneNumber);
                user.setState(State.MAIN_PAGE);
                userService.add(user);
                users.remove(user);
                getExecuteMessage("Xush kelibsiz mazgi", chatId);
            }
        }

    }

    private Message getExecuteMessage(String text, Long chatId, ReplyKeyboardMarkup shareContact) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId(chatId);
        if (shareContact != null)
            sendMessage.setReplyMarkup(shareContact);
        try {
            return execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private Message getExecuteMessage(String text, Long chatId) {
        return getExecuteMessage(text, chatId, null);
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