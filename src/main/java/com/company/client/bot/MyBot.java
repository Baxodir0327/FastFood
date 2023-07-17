package com.company.client.bot;

import com.company.server.enums.State;
import com.company.server.model.Category;
import com.company.server.model.Product;
import com.company.server.model.User;
import com.company.server.service.CategoryService;
import com.company.server.service.CreateButtonService;
import com.company.server.service.ProductService;
import com.company.server.service.UserService;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class MyBot extends TelegramLongPollingBot {
    private static UserService userService = new UserService();
    private static ProductService productService = new ProductService();
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
                    } else {
                        if (user.getState().equals(State.ENTER_NAME)) {
                            user.setFullName(text);
                            user.setState(State.PHONE_NUMBER);
                            userService.update(user);
                            ReplyKeyboardMarkup shareContactButton = createButtonService.createShareContactButton();
                            myExecute(chatId, "enter phone number",
                                    shareContactButton);

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

}
