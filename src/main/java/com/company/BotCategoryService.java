package com.company;

import com.company.server.model.Category;
import com.company.server.service.CategoryService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class BotCategoryService {
    static CategoryService categoryService = new CategoryService();

    public  List<Category> AddCategories(List<Category> categories) {

        Category category1 = new Category("\uD83D\uDE97 Buyurtma qilish",null);
        Category category2 = new Category("\uD83C\uDF7D Menyu",null);
        Category category3 = new Category("\uD83C\uDF57 Tovuq",null);
        Category category4 = new Category("\uD83C\uDF5F Kombo",null);
        Category category5 = new Category("\uD83C\uDF55 Pitsa",null);
        Category category6 = new Category("\uD83E\uDD57 Saladlar",null);
        Category category7 = new Category("\uD83C\uDF2F Lavash",null);
        Category category8 = new Category("\uD83E\uDD64 Ichimliklar",null);
        Category category9 = new Category("\uD83E\uDDF8 Bolalar uchun",null);
        Category category10 = new Category("\uD83C\uDF70 Shirinliklar",null);
        Category category11 = new Category("\uD83C\uDF45 Sous",null);
        Category category12 = new Category("\uD83C\uDF66\uD83E\uDD5B Muzqaymoq va kokteyl",null);
        Category category13 = new Category("\uD83C\uDF89 Aksiya",null);
        Category category14 = new Category("◀\uFE0F  Qaytish",null);

        List<Category> list = new ArrayList<>();

        Collections.addAll(list, category1, category2, category3, category4, category5, category6,
                category7, category8, category9, category10,
                category11, category12, category13, category14);

        list.addAll(categories);

        categoryService.writeFile(list);

        List<Category> all = categoryService.getAll();

        return all;
    }
}
