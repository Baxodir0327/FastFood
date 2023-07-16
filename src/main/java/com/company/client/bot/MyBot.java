package com.company.client.bot;

import com.company.admin.BotCategoryService;
import com.company.client.bot.BotConstants;
import com.company.server.enums.State;
import com.company.server.model.Category;
import com.company.server.model.User;
import com.company.server.service.CategoryService;
import com.company.server.service.CreateButtonService;
import com.company.server.service.UserService;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;
import java.util.stream.Collectors;

public class MyBot extends TelegramLongPollingBot {
    private static UserService userService = new UserService();
    private static CategoryService categoryService = new CategoryService();
    private static CreateButtonService createButtonService = new CreateButtonService();

    public MyBot(String botToken) {
        super(botToken);
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            Long chatId = update.getMessage().getChatId();

            Optional<User> optionalUser = userService.getByChatId(chatId);
            String username = update.getMessage().getChat().getUserName();

            User user = optionalUser.orElse(User.builder()
                    .chatId(chatId)
                    .username(username)
                    .state(State.ENTER_NAME)
                    .build());


            if (optionalUser.isEmpty()) {
                userService.add(user);
            }

            var from = message.getFrom();

            if (message.hasText()) {
                String text = message.getText();
                if (text.equals("/start") && user.getState().equals(State.ENTER_NAME)) {
                    SendMessage sendMessage = new SendMessage();
                    text = """
                            Assalomu Alaykum! Bizning botdan foydalanish uchun ro'yxatdan o'ting.
                            Hello! First you need to register to use our bot
                            Enter full name:
                            """;
                    myExecute(chatId, text);
                } else if (text.equals("/start") && user.getPhoneNumber() != null) {
                    myExecute(chatId, "Welcome " + user.getFullName());
                    mainPage(chatId, createButtonService);
                    user.setState(State.CHOOSE);
                    userService.update(user);
                } else {

                    if (user.getState().equals(State.ENTER_NAME)) {
                        user.setFullName(text);
                        user.setState(State.PHONE_NUMBER);
                        userService.update(user);
                        myExecute(chatId, "enter phone number",
                                createButtonService.createReplyButton(List.of("\uD83D\uDCDE Share contact"), true));

                    } else if (user.getState().equals(State.MAIN_PAGE)) {

                        mainPage(chatId, createButtonService);
                        user.setState(State.CHOOSE);
                        userService.update(user);

                    } else if (user.getState().equals(State.CHOOSE)) {
                        switch (text) {
                            case "\uD83D\uDECD Buyurtma berish" -> categoryPage(chatId, user);
                            case "\uD83D\uDCAC Biz biz bilan aloqa" -> chatPage(chatId, user);
                            case "⚙\uFE0F Sozlash" -> settingsPage(chatId, user);
                            default -> {
                                myExecute(chatId, "Wrong operation");
                            }
                        }
                    } else if (user.getState().equals(State.CHOOSE_CATEGORY)) {

                        List<Category> categories = categoryService.getAll();
                        String finalText = text;
                        List<String> list = categories.stream().
                                filter(category -> Objects.equals(category.getParentName(), finalText)).
                                map(Category::getName).toList();
                        ReplyKeyboardMarkup replyButton = createButtonService.createReplyButton(list, false);

                        if (list.isEmpty()) {
                            myExecute(chatId, "Bu categoryda taom mavjud emas");
                            categoryPage(chatId, user);
                        } else {
                            myExecute(chatId, "Taomni tanlang", replyButton);
                            user.setState(State.CHOOSE_PRODUCT);
                            userService.update(user);
                        }
                    } else if (user.getState().equals(State.CHOOSE_PRODUCT)) {

                    }
                }
            } else if (message.hasContact()) {

                user.setState(State.MAIN_PAGE);
                String phoneNumber = message.getContact().getPhoneNumber();
                user.setPhoneNumber(phoneNumber);
                mainPage(chatId, createButtonService);
                userService.update(user);

            }

        } else if (update.hasCallbackQuery()) {

        }
    }

    private void settingsPage(Long chatId, User user) {
        //TODO Baxodri aka
        myExecute(chatId, "Settings");


    }

    private void chatPage(Long chatId, User user) {
        //TODO Nodir aka
        myExecute(chatId, "Chat");
    }

    private void categoryPage(Long chatId, User user) {

        List<Category> allCategory = categoryService.getAll();
        List<String> categoryNames = allCategory.stream().filter(category -> category.getParentName() == null)
                .map(category -> category.getName()).toList();
        ReplyKeyboardMarkup replyButton = createButtonService.createReplyButton(categoryNames, false);
        myExecute(chatId, "Nimadan boshlaymiz " + user.getFullName(), replyButton);
        user.setState(State.CHOOSE_CATEGORY);
        userService.update(user);

    }


    private void mainPage(Long chatId, CreateButtonService createButtonService) {
        List<String> mainCategory = List.of("\uD83D\uDECD Buyurtma berish", "\uD83D\uDCAC Biz biz bilan aloqa", "⚙\uFE0F Sozlash");
        //categoryService.writeFile();
        ReplyKeyboardMarkup replyButton = createButtonService.createReplyButton(mainCategory, false);
        myExecute(chatId, "Choose ", replyButton);
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
        return BotConstants.USERNAME;
    }

}
