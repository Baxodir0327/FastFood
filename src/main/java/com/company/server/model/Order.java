package com.company.server.model;

import lombok.Data;

import java.util.UUID;
@Data
public class Order {
    private UUID id;
    private MyUser myUser;
    private String address;
}
