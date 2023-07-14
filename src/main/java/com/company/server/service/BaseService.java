package com.company.server.service;


import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.UUID;

public interface BaseService<T> {

    Gson gson = new Gson();

    T add(T t);

    List<T> getAll();

    T getById(UUID id);

    void delete(UUID id);

    T update(T t);

    void writeFile(List<T> list);

    List<T> readFile();
}
