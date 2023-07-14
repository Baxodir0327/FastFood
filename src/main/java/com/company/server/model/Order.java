package com.company.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.UUID;
@Data
public class Order {
    private UUID id;
    private MyUser myUser;
    private String address;
}
