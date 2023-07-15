package com.company.server.service;

import com.company.server.model.Product;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class ProductService implements BaseService<Product> {
    String path = "src/main/resources/product.json";

    @Override
    public Product add(Product product) {
        List<Product> products = readFile();
        products.add(product);
        writeFile(products);
        return product;
    }

    @Override
    public List<Product> getAll() {
        return readFile();
    }

    @Override
    public Product getById(UUID id) {
        return readFile().stream()
                .filter(product -> product.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public void delete(UUID id) {
        List<Product> products = readFile();
        products.removeIf(product -> product.getId().equals(id));
        writeFile(products);
    }

    @Override
    public Product update(Product product) {
        delete(product.getId());
        add(product);
        return product;
    }

    @Override
    public void writeFile(List<Product> list) {
        try {
            Files.writeString(Path.of(path), gson.toJson(list), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Product> readFile() {
        Type type = new TypeToken<List<Product>>() {
        }.getType();

        List<Product> products = null;
        try {
            products = gson.fromJson(Files.readString(Path.of(path)), type);
        } catch (IOException e) {

        }
        if (Objects.isNull(products)) {
            return new ArrayList<>();
        }
        return products;
    }

    public List<Product> getProductsByCategoryName(String text) {
        return getAll()
                .stream()
                .filter(product -> product.getCategory().getName().equals(text))
                .toList();
    }
}
