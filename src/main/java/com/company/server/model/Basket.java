package com.company.server.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;
@Data
@NoArgsConstructor
public class Basket {
    private String id;
    private LocalDateTime time;
    private List<BasketPradact> basketPradacts;
    private MyUser myUser;

    public Basket(LocalDateTime time, List<BasketPradact> basketPradacts, MyUser myUser) {
        this.id = UUID.randomUUID().toString();
        this.time = time;
        this.basketPradacts = basketPradacts;
        this.myUser = myUser;
    }
}
