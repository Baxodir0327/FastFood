package com.company.client.bot;

import com.company.server.enums.State;
import com.company.server.model.*;
import com.company.server.model.User;
import com.company.server.service.*;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.*;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class MyBot extends TelegramLongPollingBot {
    private static UserService userService = new UserService();
    private static ProductService productService = new ProductService();
    private static CategoryService categoryService = new CategoryService();
    private static CreateButtonService createButtonService = new CreateButtonService();
    private static BasketService basketService = new BasketService();
    private User user;


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

            user = optionalUser.orElse(User.builder()
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
                } else {
                    boolean isAdmin = isAdmin(user);
                    if (text.equals("/start") && user.getPhoneNumber() != null && !isAdmin) {
                        myExecute(chatId, "Welcome " + user.getFullName());
                        mainPage(chatId, createButtonService, false);
                        user.setState(State.CHOOSE);
                        userService.update(user);
                    } else if (user.getState().equals(State.CHOOSE_PRODUCT)) {
                        String f = text;
                        Product product1 = productService.getAll().stream().filter(product -> product.getName().equals(f)).findFirst().get();
                        SendPhoto sendPhoto = new SendPhoto();
                        sendPhoto.setPhoto(new InputFile(new java.io.File(product1.getPhotoUrl())));
                        sendPhoto.setChatId(chatId);
                        sendPhoto.setCaption(product1.getName() + "\n" +
                                "Narxi:" + product1.getPrice());
                        execute(sendPhoto);
                        InlineKeyboardMarkup inlineKeyboard = createButtonService.createInlineKeyboard(List.of("1", "2", "3", "4", "5", "6"), 3);
                        myExecute(chatId, "nechta kiritishni tanlang", inlineKeyboard);

                    } else if (isAdmin && text.equals("Add product")) {
                        //user.setChosenCategory(text);
                        myExecute(chatId, "Enter product name: ");
                        user.setState(State.ENTER_PRODUCT_NAME);
                        userService.update(user);
                    } else if (isAdmin && user.getState().equals(State.ENTER_PRODUCT_NAME)) {
                        //  user.setChosenCategory(text);
                        user.setLastProduct(text);
                        Product product = Product.builder()
                                .name(text)
                                .categoryName(user.getChosenCategory())
                                .build();
                        productService.add(product);
                        user.setState(State.ENTER_PRODUCT_URL);
                        ;
                        userService.update(user);


                    } else if (isAdmin && user.getState().equals(State.ENTER_PRODUCT_URL)) {
                        myExecute(chatId, "Productning rasmini yuboring:");
                        user.setState(State.SEND_PHOTO);
                        userService.update(user);
                    } else if (isAdmin && user.getState().equals(State.ENTER_PRODUCT_PRICE)) {
                        myExecute(chatId, "Enter product price:");
                        user.setState(State.SEND_PRODUCT_PRICE);
                        userService.update(user);
                    } else if (isAdmin && user.getState().equals(State.SEND_PRODUCT_PRICE)) {
                        Product product1 = productService.getAll().stream().filter(product -> Objects.equals(product.getName(), user.getLastProduct())).findFirst().get();
                        product1.setPrice(Double.parseDouble(text));
                        productService.update(product1);
                        user.setState(State.CHOOSE_CATEGORY);
                        userService.update(user);
                        categoryPage(chatId, user, isAdmin);
                    } else if (isAdmin && text.equals("Add Category")) {
                        myExecute(chatId, "Enter Category name: ");
                        user.setState(State.ENTER_CATEGORY_NAME);
                        userService.update(user);
                    } else if (isAdmin && text.equals("Delete Category")) {
                        myExecute(chatId, "Enter Delete Category name");
                        user.setState(State.DELETE_CATEGORY);
                        userService.update(user);
                    } else if (isAdmin && user.getState().equals(State.DELETE_CATEGORY)) {
                        List<Category> categories = categoryService.getAll();
                        String finalCategory = text;
                        categories.removeIf(category -> category.getName().equalsIgnoreCase(finalCategory));
                        categoryService.writeFile(categories);
                        user.setState(State.CHOOSE);
                        userService.update(user);
                        mainPage(chatId, createButtonService, true);
                    } else if (isAdmin && user.getState().equals(State.ENTER_CATEGORY_NAME)) {
                        Category category = new Category(text, "MainPage");
                        categoryService.add(category);
                        myExecute(chatId, text + " category added");
                        user.setState(State.CHOOSE);
                        userService.update(user);
                        mainPage(chatId, createButtonService, true);
                    } else if (text.equals("/start") && user.getPhoneNumber() != null && isAdmin) {
                        myExecute(chatId, "Welcome Admin");
                        mainPage(chatId, createButtonService, true);
                        user.setState(State.CHOOSE);
                        userService.update(user);
                    } else if (isAdmin && text.equals("+ Add Category")) {
                        myExecute(chatId, "Enter Category name: ");
                        user.setState(State.CREATE_CHILD_CATEGORY);
                        userService.update(user);
                    } else if (isAdmin && user.getState().equals(State.CREATE_CHILD_CATEGORY)) {
                        Category category = new Category(text, null);
                        categoryService.add(category);
                        myExecute(chatId, text + "category added");
                        user.setState(State.CHOOSE_CATEGORY);
                        userService.update(user);
                    } else if (isAdmin && text.equals("- Delete Category")) {
                        myExecute(chatId, "Enter delete category name: ");
                        user.setState(State.DELETE_CHILD_CATEGORY);
                        userService.update(user);
                    } else if (isAdmin && user.getState().equals(State.DELETE_CHILD_CATEGORY)) {
                        List<Category> categories = categoryService.getAll();
                        String finalText1 = text;
                        categories.removeIf(category -> category.getName().equalsIgnoreCase(finalText1));
                        categoryService.writeFile(categories);
                        user.setState(State.CHOOSE_CATEGORY);
                        userService.update(user);
                        categoryPage(chatId, user, true);
                    } else if (user.getState().equals(State.CHOOSE_CATEGORY) && text.equals(".\uD83D\uDDD1 Savat")) {
                        System.out.println("savat");
                        List<Basket> basketList = basketService.getAll();
                        Optional<Basket> basketOptional = basketList.stream().filter(basket -> basket.getMyUser().getChatId().equals(user.getChatId())).findFirst();
                        System.out.println(basketOptional.isPresent());
                        if (basketOptional.isPresent()) {
                            Basket basket = basketOptional.get();

                            List<BasketProduct> basketProducts = basket.getBasketProducts();
                            StringBuffer sb = new StringBuffer();
                            double summa = 0d;
                            for (BasketProduct basketProduct : basketProducts) {
                                double price = basketProduct.getProduct().getPrice();
                                int count = basketProduct.getCount();
                                summa = price * count + summa;
                                sb.append(basketProduct.getProduct().getName()).append("\n").append(count).append(" * ").append(price).append(" = ").append(price * count).append("\n");
                            }
                            sb.append("\n\n").append("Umumiy:").append(summa);

                            List<String> productList = basketProducts.stream().map(basketProduct -> basketProduct.getProduct().getName()).collect(Collectors.toList());
                            productList.addAll(List.of("Qaytish", "Tozalsh"));
                            ReplyKeyboardMarkup replyButton = createButtonService.createReplyButton(productList, false);
                            myExecute(chatId, sb.toString(), replyButton);
                            
                        } else {
                            myExecute(chatId, "Savatchangiz bo'sh");
                        }

                    } else if (user.getState().equals(State.CHOOSE_CATEGORY) && text.equals("\uD83C\uDF7D Menyu")) {

                        String path1 = "src/main/resources/menuPhoto/img.png";
                        sendPhoto(chatId, path1);

                        String path2 = "src/main/resources/menuPhoto/img_1.png";
                        sendPhoto(chatId, path2);

                        String path3 = "src/main/resources/menuPhoto/img_2.png";
                        sendPhoto(chatId, path3);

                        String path4 = "src/main/resources/menuPhoto/img_3.png";
                        sendPhoto(chatId, path4);

                    } else if (user.getState().equals(State.CHOOSE_CATEGORY) && text.equals("\uD83D\uDE97 Buyurtma qilish")) {
                        sendLocation(text, message, chatId);
                    } else if (text.equals("◀\uFE0F Qaytish")) {
                        user.setState(State.CHOOSE_CATEGORY);
                        categoryPage(chatId, user, isAdmin);
                    }  else{


                        if (user.getState().equals(State.ENTER_NAME)) {
                            user.setFullName(text);
                            user.setState(State.PHONE_NUMBER);
                            userService.update(user);
                            myExecute(chatId, "enter phone number",
                                    createButtonService.createReplyButton(List.of("\uD83D\uDCDE Share contact"), true));

                        } else if (user.getState().equals(State.MAIN_PAGE)) {

                            mainPage(chatId, createButtonService, isAdmin);
                            user.setState(State.CHOOSE);
                            userService.update(user);

                        } else if (user.getState().equals(State.CHOOSE)) {
                            switch (text) {
                                case "\uD83D\uDECD Buyurtma berish" -> categoryPage(chatId, user, isAdmin);
                                case "\uD83D\uDCAC Biz biz bilan aloqa" -> chatPage(chatId, user);
                                case "⚙\uFE0F Sozlash" -> settingsPage(chatId, user);
                                default -> {
                                    myExecute(chatId, "Wrong operation");
                                }
                            }
                        } else if (user.getState().equals(State.CHOOSE_CATEGORY)) {

                           /* Category addCategory = new Category("Add product","adminProduct");
                            Category deleteCategory = new Category("Delete product","adminProduct");

                            categoryService.add(addCategory);
                            categoryService.add(deleteCategory);*/
                            String finalText = text;
                            System.out.println(text);
                            user.setChosenCategory(text);
                            userService.update(user);

                            List<Product> products = productService.getAll();
                            List<String> productsNames = products.stream()
                                    .filter(categoryName -> Objects.equals(categoryName.getCategoryName(), finalText))
                                    .map(Product::getName)
                                    .collect(Collectors.toList());
                            System.out.println(productsNames);

                            if (isAdmin) {
                                List<String> adminProduct = categoryService.getAll().stream().filter(category -> Objects.equals(category.getParentName(), "adminProduct")).map(Category::getName).toList();
                                productsNames.addAll(adminProduct);
                            }
                            ReplyKeyboardMarkup replyButton = createButtonService.createReplyButton(productsNames, false);

                            if (productsNames.isEmpty()) {
                                myExecute(chatId, "Bu categoryda taom mavjud emas");
                                categoryPage(chatId, user, isAdmin);
                            } else {
                                myExecute(chatId, "Taomni tanlang", replyButton);
                                user.setState(State.CHOOSE_PRODUCT);
                                userService.update(user);
                            }
                        }
                    }
                }
            } else if (message.hasContact()) {

                user.setState(State.MAIN_PAGE);
                String phoneNumber = message.getContact().getPhoneNumber();
                boolean isAdmin = phoneNumber.equals("+998931419445") || phoneNumber.equals("+998911638343");
                user.setPhoneNumber(phoneNumber);
                userService.update(user);
                mainPage(chatId, createButtonService, isAdmin);

            } else if (message.hasLocation()) {
                user.setState(State.CONFIRMATION);
                sendMessage("share contact", chatId);
            } else if (message.hasPhoto() && isAdmin(user) && user.getState().equals(State.SEND_PHOTO)) {
                System.out.println("rasm");
                PhotoSize photo = message.getPhoto().stream().sorted((o1, o2) -> o2.getWidth() * o2.getHeight() - o1.getWidth() * o1.getHeight())
                        .findFirst()
                        .orElse(null);
                if (photo != null) {
                    System.out.println("Rasm");
                    GetFile getFile = new GetFile();
                    getFile.setFileId(photo.getFileId());
                    try {
                        File file = execute(getFile);
                        String filePath = (file).getFilePath();
                        System.out.println(filePath);
                        String fileUrl = "https://api.telegram.org/file/bot" + BotConstants.TOKEN + "/" + filePath;
                        String savePath = "src/main/resources/" + filePath;
                        saveImageFromUrl(fileUrl, savePath);
                        Optional<Product> optionalProduct = productService.getAll().stream().filter(product -> Objects.equals(product.getName(), user.getLastProduct())).findFirst();
                        Product product = optionalProduct.get();
                        product.setPhotoUrl(savePath);
                        productService.update(product);
                        user.setState(State.ENTER_PRODUCT_PRICE);
                        userService.update(user);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (update.hasCallbackQuery()) {
        }
    }

    private void sendMessage(String text, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId(chatId);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendPhoto(Long chatId, String pathname) {
        java.io.File file = new java.io.File(pathname);
        InputFile inputFile = new InputFile(file);
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(inputFile);

        try {
            execute(sendPhoto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isAdmin(User user) {
        boolean isAdmin = Objects.equals(user.getPhoneNumber(), "+998931419445") || Objects.equals(user.getPhoneNumber(), "+998911638343");
        return isAdmin;
    }

    private void saveImageFromUrl(String fileUrl, String savePath) throws IOException {
        URL url = new URL(fileUrl);
        try (InputStream inputStream = url.openStream()) {
            Path outputPath = Path.of(savePath);
            Files.copy(inputStream, outputPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void settingsPage(Long chatId, User user) {
    }

    private void chatPage(Long chatId, User user) {
        String txt = """
                @EnU098 (https://t.me/EnU098)
                ☎ Call +998910339823
                                
                Ushbu kontaktlar orqali biz biln bog'lanishingiz mumkin
                """;

        myExecute(chatId, txt);
    }

    private void categoryPage(Long chatId, User user, boolean isAdmin) {

        /*Category addCategory = new Category("Add Category", "adminCategory");
        Category deleteCategory = new Category("Delete Category", "adminCategory");
        categoryService.add(addCategory);
        categoryService.add(deleteCategory);*/

        List<Category> allCategory = categoryService.getAll();
        List<String> categoryNames = allCategory.stream().filter(category -> category.getParentName() == null)
                .map(Category::getName).collect(Collectors.toList());
        if (isAdmin) {
            List<String> adminCategory = allCategory.stream().filter(category -> Objects.equals(category.getParentName(), "adminCategory")).map(category -> category.getName()).toList();
            categoryNames.addAll(adminCategory);
        }
        ReplyKeyboardMarkup replyButton = createButtonService.createReplyButton(categoryNames, false);
        myExecute(chatId, "Nimadan boshlaymiz " + user.getFullName(), replyButton);
        user.setState(State.CHOOSE_CATEGORY);
        userService.update(user);

    }


    private void mainPage(Long chatId, CreateButtonService createButtonService, boolean isAdmin) {

       /* List<String> mainCategory = List.of("\uD83D\uDECD Buyurtma berish", "\uD83D\uDCAC Biz biz bilan aloqa", "⚙\uFE0F Sozlash");
        Category category1 = new Category("\uD83D\uDECD Buyurtma berish", "MainPage");
        Category category2 = new Category("\uD83D\uDCAC Biz biz bilan aloqa", "MainPage");
        Category category3 = new Category("⚙\uFE0F Sozlash", "MainPage");
        categoryService.add(category1);
        categoryService.add(category2);
        categoryService.add(category3);*/
        //categoryService.writeFile();

      /*  Category mainAdminCategory = new Category("Add Category", "adminMainPage");
        categoryService.add(mainAdminCategory);*/

       /* Category category1 = new Category("Delete Category", "adminMainPage");
        categoryService.add(category1);*/


        List<Category> mainButtons = categoryService.getAll();
        List<String> buttons = mainButtons.stream().filter(category -> Objects.equals(category.getParentName(), "MainPage")).map(category -> category.getName()).collect(Collectors.toList());

        if (isAdmin) {
            List<String> admin = mainButtons.stream().filter(category -> Objects.equals(category.getParentName(), "adminMainPage")).map(Category::getName).toList();
            buttons.addAll(admin);
        }


        ReplyKeyboard replyButton = createButtonService.createReplyButton(buttons, false);
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

    private void sendLocation(String text, Message message, Long chatId) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Share Location");

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        KeyboardRow row1 = new KeyboardRow();

        KeyboardButton keyboardButton = new KeyboardButton();
        KeyboardButton keyboardButton1 = new KeyboardButton();
        keyboardButton1.setText("◀\uFE0F Qaytish");
        row1.add(keyboardButton1);
        keyboardButton.setText("\uD83D\uDCCD Turgan joyimni jo'natish");
        keyboardButton.setRequestLocation(true);
        row.add(keyboardButton);
        rows.add(row);
        rows.add(row1);

        replyKeyboardMarkup.setKeyboard(rows);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }
}


