package com.company.server.service;

import com.company.server.model.Category;
import com.company.server.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

// Nodir aka yozyapti
public class CategoryService implements BaseService<Category> {
    Path path = Path.of("src/main/resources/category.json");
    @Override
    public Category add(Category category) {
        List<Category> categories = readFile();
        categories.add(category);
        writeFile(categories);
        return category;
    }

    @Override
    public List<Category> getAll() {
        return readFile();
    }

    @Override
    public Category getById(UUID id) {
        return readFile().stream()
                .parallel()
                .filter(category -> category.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public void delete(UUID id) {
        List<Category> categories = readFile();
        categories.removeIf(category -> category.getId().equals(id));
        writeFile(categories);
    }

    @Override
    public Category update(Category category) {
        delete(category.getId());
        add(category);
        return category;
    }

    @Override
    public void writeFile(List<Category> list) {
        try {
            Files.writeString(path, gson.toJson(list), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Category> readFile() {
        try {
            List<Category> categories = gson.fromJson(Files.readString(path), new TypeToken<List<Category>>() {
            }.getType());
            if (Objects.isNull(categories)){
                return new ArrayList<>();
            }
            return categories;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
