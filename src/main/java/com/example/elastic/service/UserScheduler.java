package com.example.elastic.service;

import com.example.elastic.entity.User;
import com.example.elastic.payload.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserScheduler {
    private static final Logger logger = LoggerFactory.getLogger(UserScheduler.class);

    private UserDto user;

    @Autowired
    private UserService userService;

    public void registerUser(UserDto user){
        this.user = user;
    }

    @Scheduled(fixedDelay = 10000)
    public void getUsers(){
        List<UserDto> users = (List<UserDto>) userService.getAllUsers();
        for(UserDto u : users){
            System.out.println(u.getName());
        }
    }

    @Scheduled(fixedDelay = 1000 * 30)
    public void saveUser(){
        if(user != null){
            User createdUser = userService.registerNewUser(user);
            logger.info("user registered at: {}", new Date());
            user = null;
        }
    }
}
