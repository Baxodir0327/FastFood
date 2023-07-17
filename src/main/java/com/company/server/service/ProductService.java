package com.company.server.service;

import com.company.server.model.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class ProductService{
    String path = "src/main/resources/product.json";
    Gson gson = new Gson();

    public Product add(Product product) {
        List<Product> products = readFile();
        products.add(product);
        writeFile(products);
        return product;
    }

    public List<Product> getAll() {
        return readFile();
    }

    public Product getById(UUID id) {
        return readFile().stream()
               .filter(product -> product.getId().equals(id)).findFirst().orElse(null);
    }

    public void delete(UUID id) {
        List<Product> products = readFile();
        products.removeIf(product -> product.getId().equals(id));
        writeFile(products);
    }

    public Product update(Product product) {
        delete(product.getId());
        add(product);
        return product;
    }

    public void writeFile(List<Product> list) {
        try {
            Files.writeString(Path.of(path), gson.toJson(list), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    public List<Product> readFile() {
        return gson.fromJson(Files.readString(Path.of(path)), new TypeToken<List<Product>>() {}.getType());
    }

    public List<Product> getProductsByCategoryName(String text) {
        return getAll()
                .stream()
                .filter(product -> product.getName().equals(text))
                .toList();
    }
}
