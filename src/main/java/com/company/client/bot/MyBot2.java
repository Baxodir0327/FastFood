package com.company.client.bot;

import com.company.server.convert.UserConverter;
import com.company.server.enums.State;
import com.company.server.model.User;
import com.company.server.service.CategoryService;
import com.company.server.service.CreateButtonService;
import com.company.server.service.ProductService;
import com.company.server.service.UserService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import static com.company.client.bot.BotConstants.*;
public class MyBot2 extends TelegramLongPollingBot {
    private static UserService userService = new UserService();
    private static UserConverter userConverter = new UserConverter();
    private static ProductService productService = new ProductService();
    private static CategoryService categoryService = new CategoryService();
    private static CreateButtonService createButtonService = new CreateButtonService();


    public MyBot2(String botToken) {
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            String userName = message.getChat().getUserName();

            User user = userConverter.convertToEntity(chatId, userName);

            if (message.hasText()) {
                String text = message.getText();

                if (user.getState().equals(State.START) && text.equals("/start")) {
                    myExecute(chatId, FIRST_MSG);
                    user.setState(State.ENTER_NAME);
                    userService.update(user);
                } else if (user.getState() == State.ENTER_NAME) {
                    user.setFullName(text);
                    user.setState(State.PHONE_NUMBER);
                    userService.update(user);
                    myExecute(chatId, "enter phone number",
                            createButtonService.createReplyButton(List.of("\uD83D\uDCDE Share contact"), true));
                }
            } else if (message.hasContact()) {
                user.setState(State.MAIN_PAGE);
                String phoneNumber = message.getContact().getPhoneNumber();
                user.setPhoneNumber(phoneNumber);
                userService.update(user);
                var page = new Page();
                ReplyKeyboard replyKeyboard = page.mainPage(createButtonService, isAdmin(phoneNumber));
                myExecute(chatId, "choose", replyKeyboard);

            }
        } else if (update.hasCallbackQuery()) {

        }

    }

    private static boolean isAdmin(String phoneNumber) {
        return ADMIN_NUMBERS.contains(phoneNumber);
    }

    private Message myExecute(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage(chatId.toString(), text);
        try {
            return execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void myExecute(Long chatId, String message, ReplyKeyboard r) {
        SendMessage s = new SendMessage();
        s.setChatId(chatId);
        s.setText(message);
        s.setReplyMarkup(r);
        try {
            execute(s);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return USERNAME;
    }
}
