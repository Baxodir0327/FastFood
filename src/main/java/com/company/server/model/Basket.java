package com.company.server.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;
@Data
@NoArgsConstructor
public class Basket {
    private String id;
    private Date date;
    private List<BasketProduct> basketProducts;
    private MyUser myUser;

    public Basket(Date date, List<BasketProduct> basketProducts, MyUser myUser) {
        this.id = UUID.randomUUID().toString();
        this.date = date;
        this.basketProducts = basketProducts;
        this.myUser = myUser;
    }
}
