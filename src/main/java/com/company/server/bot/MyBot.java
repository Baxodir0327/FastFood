package com.company.server.bot;

import com.company.server.enums.State;
import com.company.server.model.Category;
import com.company.server.model.Product;
import com.company.server.service.CategoryService;
import com.company.server.service.ProductService;
import com.company.server.service.UserService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import com.company.server.model.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

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
            User user;
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
                    CategoryService categoryService = new CategoryService();

                    categories(chatId, user, categoryService);

                } else if (user.getState().equals(State.CHOOSE_CATEGORY)) {
                    if (text.equals("Add category") && isAdmin(chatId)) {
                        getExecuteMessage("Enter category name", chatId);
                        user.setState(State.ENTER_CATEGORY_NAME);
                        userService.update(user);
                    } else {
                        //categories(chatId, user, new CategoryService());
                        ProductService productService = new ProductService();
                        List<Product> products=productService.getProductsByCategoryName(text);
                        List<String> list = products.stream().map(Product::getName).toList();
                        ArrayList<String> strings = new ArrayList<>(list);
                        strings.add("Add product");
                        //list.add("ADD product");
                        InlineKeyboardMarkup inlineKeyboard = createButtonService.createInlineKeyboard(strings, 2);
                        getExecuteMessage("choose product",chatId,inlineKeyboard);
                    }
                } else if (user.getState().equals(State.ENTER_CATEGORY_NAME)) {
                    CategoryService categoryService = new CategoryService();
                    categoryService.add(new Category(text));
                    user.setState(State.MAIN_PAGE);
                    userService.update(user);

                    getExecuteMessage("main page", chatId);

                    categories(chatId, user, categoryService);
                }
            }
            if (message.hasContact() && user.getState().equals(State.PHONE_NUMBER)) {
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

    private void categories(Long chatId, User user, CategoryService categoryService) {
        List<Category> all = categoryService.getAll();

        List<String> nameList = new ArrayList<>(all.stream().map(Category::getName).toList());
        if (isAdmin(chatId)) {
            nameList.add("Add category");
        }
        nameList.addAll(0, List.of("Buyurtma berish", "Menyu", "Setings"));

        ReplyKeyboardMarkup replyButton = createButtonService.createReplyButton(nameList, false);
        getExecuteMessage("Tanlang", chatId, replyButton);
        user.setState(State.CHOOSE_CATEGORY);
        userService.update(user);
    }

    private static boolean isAdmin(Long chatId) {
        return chatId == 1806459310;
    }

    private Message getExecuteMessage(String text, Long chatId, ReplyKeyboard shareContact) {
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