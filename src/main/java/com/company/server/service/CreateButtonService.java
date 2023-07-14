package com.company.server.service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

//Doniyor yozyapti
public class CreateButtonService {
    public ReplyKeyboardMarkup createReplyButton(List<String> buttonsTitle) {

        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        for (int i = 0; i < buttonsTitle.size(); i++) {
            if (i != 0 && i % 2 == 0) {
                rows.add(row);
                row = new KeyboardRow();
            }
            row.add(new KeyboardButton(buttonsTitle.get(i)));

        }

        rows.add(row);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        replyKeyboardMarkup.setKeyboard(rows);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    public InlineKeyboardMarkup createInlineKeyboard(List<String> keyboasrdList,int numberOfRows) {

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        int maxNumber = keyboasrdList.size();
        int currentNumber = 1;

        while (currentNumber <= maxNumber) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int i = 0; i < numberOfRows && currentNumber <= maxNumber; i++) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(keyboasrdList.get(i));
                button.setCallbackData(String.valueOf(currentNumber));
                row.add(button);
                currentNumber++;
            }
            rows.add(row);
        }

        keyboardMarkup.setKeyboard(rows);

        return keyboardMarkup;
    }
}
