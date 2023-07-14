package com.company.server.service;

import com.company.server.model.Product;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

// Bilol yozyapti
public class ProductService implements BaseService<Product>{
    Path path = Path.of("src/main/java/resources/product.json");

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
    public Optional<Product> getById(Long id) {
        return readFile().stream()
                .filter(product -> product.getUuid().equals(id)).findFirst();
    }

    @Override
    public void delete(UUID id) {
         List<Product> products = readFile();
         products.removeIf(product -> product.getUuid().equals(id));
         writeFile(products);
    }

    @Override
    public Product update(Product product) {
        delete(product.getUuid());
        add(product);
        return product;
    }

    @Override
    public void writeFile(List<Product> list) {
        try {
            Files.writeString(path, gson.toJson(list), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Product> readFile() {
        Type type = new TypeToken<List<Product>>() {
        }.getType();
        List<Product> products = gson.fromJson(String.valueOf(path), type);
        if (Objects.isNull(products)){
            return Collections.EMPTY_LIST;
        }
        return products;
    }
}
