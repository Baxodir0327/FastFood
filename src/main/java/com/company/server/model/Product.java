package com.company.server.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product{
    private UUID uuid = UUID.randomUUID();
    private String name;
    private String photoUrl;
    private double price;
    private Category category ;
}
