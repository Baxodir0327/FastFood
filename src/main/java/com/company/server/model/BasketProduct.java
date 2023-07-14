package com.company.server.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor

public class BasketProduct{
    private final UUID uuid = UUID.randomUUID();
    private Product product;
    private Integer count;
    private boolean clientAdded;
}
