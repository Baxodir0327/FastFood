package com.company.server.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Product{
    private Integer id;

    private String name;

    private String photoUrl;

    private double price;

    private Category category;
}
