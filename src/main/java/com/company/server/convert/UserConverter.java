package com.company.server.convert;


import com.company.server.enums.State;
import com.company.server.model.User;
import com.company.server.service.UserService;

import java.util.Optional;


public class UserConverter {
    private  static UserService userService=new UserService();
    public User convertToEntity(Long chatId,String username) {
        Optional<User> optionalUser = userService.getByChatId(chatId);

        User user = optionalUser.orElse(User.builder()
                .chatId(chatId)
                .username(username)
                .state(State.START)
                .build());


        if (optionalUser.isEmpty()) {
            userService.add(user);
        }
        return user;
    }
}
