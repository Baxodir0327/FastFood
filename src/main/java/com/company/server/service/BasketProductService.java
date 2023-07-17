package com.company.server.service;

import com.company.server.model.Basket;
import com.company.server.model.BasketProduct;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.UUID;

public class BasketProductService implements BaseService<BasketProduct> {
    private Path path = Path.of("src/main/resources/basketProduct.json");

    @Override
    public BasketProduct add(BasketProduct basketProduct) {
        List<BasketProduct> basketList = readFile();
        basketList.add(basketProduct);
        writeFile(basketList);
        return basketProduct;
    }

    @Override
    public List<BasketProduct> getAll() {
        return readFile();
    }

    @Override
    public BasketProduct getById(UUID id) {
        return readFile().stream()
                .parallel()
                .filter(basketProduct -> basketProduct.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public void delete(UUID id) {
        List<BasketProduct> basketProductsList = readFile();
        basketProductsList.removeIf(basket -> basket.getId().equals(id));
        writeFile(basketProductsList);
    }

    @Override
    public BasketProduct update(BasketProduct basketProduct) {
        delete(basketProduct.getId());
        add(basketProduct);
        return basketProduct;
    }

    @Override
    @SneakyThrows
    public void writeFile(List<BasketProduct> list) {
        Files.writeString(path, gson.toJson(list), StandardOpenOption.TRUNCATE_EXISTING);
    }

    @Override
    @SneakyThrows
    public List<BasketProduct> readFile() {
        return gson.fromJson(Files.readString(path), new TypeToken<List<Basket>>() {
        }.getType());
    }
}
