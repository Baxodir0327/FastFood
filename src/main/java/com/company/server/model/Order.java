package com.company.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private final UUID id = UUID.randomUUID();
    private MyUser myUser;
    private String address;

}
