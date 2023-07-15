package com.company.server.model;

import com.company.server.model.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class BasketProduct extends BaseModel {
    private Product product;
    private int count;
}
