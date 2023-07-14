package com.company.server.model;

import com.company.server.enums.States;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MyUser {
    private UUID id = UUID.randomUUID();
    private String fullName;
    private String username;
    private String phoneNumber;
    private Long chatId;
    private LocalDateTime created;
    private LocalDateTime update;
    private States states;

}
