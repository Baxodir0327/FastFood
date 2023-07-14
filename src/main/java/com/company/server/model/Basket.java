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
    private List<BasketPradact> basketPradacts;
    private MyUser myUser;

    public Basket(Date date, List<BasketPradact> basketPradacts, MyUser myUser) {
        this.id = UUID.randomUUID().toString();
        this.date = date;
        this.basketPradacts = basketPradacts;
        this.myUser = myUser;
    }
}
