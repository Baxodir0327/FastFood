package com.company.server.service;

import com.company.server.model.Category;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.UUID;

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

    @SneakyThrows
    @Override
    public void writeFile(List<Category> list) {
        Files.writeString(path, gson.toJson(list), StandardOpenOption.TRUNCATE_EXISTING);
    }

    @SneakyThrows
    @Override
    public List<Category> readFile() {
        return gson.fromJson(Files.readString(path), new TypeReference<List<Category>>() {
        }.getType());
    }
}
