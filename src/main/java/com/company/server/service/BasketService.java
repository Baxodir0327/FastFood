package com.company.server.service;

import com.company.server.model.Basket;
import com.company.server.model.Basket;
import com.company.server.model.User;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.UUID;

public class BasketService implements BaseService<Basket> {
    private Path path = Path.of("src/main/resources/basket.json");

    @Override
    public Basket add(Basket basket) {
        List<Basket> basketList = readFile();
        basketList.add(basket);
        writeFile(basketList);
        return basket;
    }
    @Override
    public List<Basket> getAll() {
        return readFile();
    }

    @Override
    public Basket getById(UUID id) {
        return readFile().stream()
                .parallel()
                .filter(basket -> basket.getId().equals(id)).findFirst().orElse(null);
    }
    public Basket getByUserId(UUID userId){
        return  readFile().stream().filter(basket -> basket.getMyUser().getId().equals(userId)).findFirst().orElse(null);
    }



    @Override
    public void delete(UUID id) {
        List<Basket> basketList = readFile();
        basketList.removeIf(basket -> basket.getId().equals(id));
        writeFile(basketList);
    }

    @Override
    public Basket update(Basket basket) {
        delete(basket.getId());
        add(basket);
        return basket;
    }

    @SneakyThrows
    @Override
    public void writeFile(List<Basket> list) {
        Files.writeString(path, gson.toJson(list), StandardOpenOption.TRUNCATE_EXISTING);
    }

    @SneakyThrows
    @Override
    public List<Basket> readFile() {
        return gson.fromJson(Files.readString(path), new TypeToken<List<Basket>>() {
        }.getType());
    }
}
