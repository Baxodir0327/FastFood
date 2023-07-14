package com.company.server.service;


import com.company.server.model.MyUser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class UserService implements BaseService<MyUser> {
    Path path = Path.of("src/main/java/resources/users.json");

    @Override
    public MyUser add(MyUser user) {
        List<MyUser> users = readFile();
        users.add(user);
        writeFile(users);
        return user;
    }

    @Override
    public List<MyUser> getAll() {
        return readFile();
    }

    @Override
    public MyUser getById(UUID id) {
        return readFile().stream()
                .filter(user -> user.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public void delete(UUID id) {
        List<MyUser> users = readFile();
        users.removeIf(user -> user.getId().equals(id));
        writeFile(users);
    }

    @Override
    public MyUser update(MyUser user) {
        user.setUpdate(LocalDateTime.now());
        delete(user.getId());
        add(user);
        return user;
    }

    @Override
    public void writeFile(List<MyUser> list) {

        try {
            Files.writeString(path, gson.toJson(list), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<MyUser> readFile() {

        try {
            List<MyUser> users = gson.fromJson(Files.readString(path), new TypeToken<List<MyUser>>() {
            }.getType());
            if (Objects.isNull(users)) {
                return new ArrayList<>();
            }
            return users;
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
