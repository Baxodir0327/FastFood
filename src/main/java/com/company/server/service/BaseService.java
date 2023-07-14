package com.company.server.service;


import com.google.gson.Gson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BaseService<T> {

    Gson gson = new Gson();

    T add(T t);

    List<T> getAll();

    Optional<T> getById(Long id);

    void delete(UUID id);

    T update(T t);

    void writeFile(List<T> list);

    List<T> readFile();
}
