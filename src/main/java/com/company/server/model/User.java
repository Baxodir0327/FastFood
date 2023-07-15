package com.company.server.model;

import com.company.server.enums.State;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseModel{
        private String fullName;
        private String username;
        private String phoneNumber;
        private Long chatId;
        private State state;
    }

