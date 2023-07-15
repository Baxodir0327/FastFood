package com.company.server.service;


import com.company.server.model.Order;
import com.company.server.model.User;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderService implements BaseService<Order> {
    private Path path = Path.of("src/main/resources/order.json");

    @Override
    public Order add(Order order) {
        List<Order> orderList = readFile();
        orderList.add(order);
        writeFile(orderList);
        return order;
    }

    @Override
    public List<Order> getAll() {
        return readFile();
    }

    @Override
    public Order getById(UUID id) {
        return readFile().stream()
                .parallel()
                .filter(order -> order.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public void delete(UUID id) {
        List<Order> orderList = readFile();
        orderList.removeIf(order -> order.getId().equals(id));
        writeFile(orderList);

    }

    @Override
    public Order update(Order order) {
        delete(order.getId());
        add(order);
        return order;
    }

    @SneakyThrows
    @Override
    public void writeFile(List<Order> list) {
        Files.writeString(path, gson.toJson(list), StandardOpenOption.TRUNCATE_EXISTING);

    }

    @SneakyThrows
    @Override
    public List<Order> readFile() {
        return gson.fromJson(Files.readString(path), new TypeToken<List<Order>>() {
        }.getType());
    }
}
