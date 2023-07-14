package com.company.server.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class BasketPradact {
    private Product product;

    private Integer count;

    private boolean clientAdded;
}
