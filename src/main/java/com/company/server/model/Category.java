package com.company.server.model;

import lombok.Data;
import java.util.UUID;

@Data
public class Category {
    private final UUID id=UUID.randomUUID();
    private String name;
    private Category parent;
    private boolean inline;

}
