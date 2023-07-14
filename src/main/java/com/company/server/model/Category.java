package com.company.server.model;

import lombok.Data;
import java.util.UUID;

@Data
public class Category {
    private final UUID id;

    private String name;

    private Category parent;

    private boolean inline;

}
