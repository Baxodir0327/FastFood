package com.company.server.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;
@Data
public class Basket {
    private final String id;
    private LocalDateTime time;
    private List<BasketProduct> basketPradacts;
    private MyUser myUser;

    public Basket(LocalDateTime time, List<BasketProduct> basketPradacts, MyUser myUser) {
        this.id = UUID.randomUUID().toString();
        this.time = time;
        this.basketPradacts = basketPradacts;
        this.myUser = myUser;
    }
}
