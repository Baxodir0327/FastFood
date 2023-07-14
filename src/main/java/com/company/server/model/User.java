package com.company.server.model;

import com.company.server.enums.State;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseModel{
    private String fullName;
    private String username;
    private String phoneNumber;
    private Long chatId;
    private State state;
}
