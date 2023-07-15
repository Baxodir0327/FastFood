package com.company.server.service;

import com.company.server.model.User;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class UserService implements BaseService<User> {
    Path path = Path.of("src/main/resources/user.json");

    @Override
    public User add(User user) {
        List<User> users = readFile();
        users.add(user);
        writeFile(users);
        return user;
    }

    @Override
    public List<User> getAll() {
        return readFile();
    }

    @Override
    public User getById(UUID id) {
        return readFile().stream()
                .parallel()
                .filter(user -> user.getId().equals(id)).findFirst().orElse(null);
    }

    public Optional<User> getById(Long id) {
        return readFile().stream()
                .parallel()
                .filter(user -> user.getChatId().equals(id)).findFirst();
    }

    @Override
    public void delete(UUID id) {
        List<User> users = readFile();
        users.removeIf(user -> user.getId().equals(id));
        writeFile(users);
    }

    @Override
    public User update(User user) {
        delete(user.getId());
        add(user);
        return user;
    }

    @SneakyThrows
    @Override
    public void writeFile(List<User> list) {
        Files.writeString(path, gson.toJson(list), StandardOpenOption.WRITE);
    }

    @Override
    public List<User> readFile() {
        try {
            List<User> users = gson.fromJson(Files.readString(path), new TypeToken<List<User>>() {}.getType());
            if (Objects.isNull(users)) {
                return new ArrayList<>();
            }
            return users;
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
