package com.company.server.model;

import lombok.Data;
import java.util.UUID;

@Data
public class Order {
    private final String id;
    private MyUser myUser;
    private String adress;

    public Order(MyUser myUser, String adress) {
        this.id = UUID.randomUUID().toString();
        this.myUser = myUser;
        this.adress = adress;
    }

}
