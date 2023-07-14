package com.company.server.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Basket {
    private final UUID id=UUID.randomUUID();
    private LocalDateTime time;
    private List<BasketProduct> basketPradacts;
    private MyUser myUser;
}
