package com.company.client.bot;

import com.company.server.service.CategoryService;
import com.company.server.service.CreateButtonService;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.List;

public class Page {
    private static CategoryService categoryService = new CategoryService();

    public ReplyKeyboard mainPage(CreateButtonService createButtonService, boolean admin) {
        List<String> mainButtons = categoryService.getMainPageCategory(admin);
        return createButtonService.createReplyButton(mainButtons, false);
    }
}
