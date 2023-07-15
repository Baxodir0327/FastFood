package com.company.server.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BasketProduct extends BaseModel {
    private Product product;
    private int count;
}
