package com.company.server.model;

import lombok.Getter;
import lombok.ToString;
import org.apache.http.client.utils.URIUtils;

import java.util.UUID;

@Getter
@ToString
public abstract class BaseModel {
    protected final UUID id = UUID.randomUUID();
}
