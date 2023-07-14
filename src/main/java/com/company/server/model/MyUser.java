package com.company.server.model;

import com.company.server.enums.States;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class MyUser {
    private UUID id;
    private String fullName;
    private String username;
    private String phoneNumber;
    private Long chatId;
    private LocalDateTime created;
    private LocalDateTime update;
    private States states;

}
