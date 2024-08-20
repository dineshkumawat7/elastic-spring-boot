package com.example.elastic.service;

import com.example.elastic.document.UserDoc;
import com.example.elastic.payload.UserDto;
import com.example.elastic.repository.elastic.ElasticUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserScheduler {
    private static final Logger logger = LoggerFactory.getLogger(UserScheduler.class);

    private UserDto user;

    @Autowired
    private ElasticUserRepository elasticUserRepository;
    @Autowired
    private UserService userService;

    public void registerUser(UserDto user){
        this.user = user;
    }

    @Scheduled(fixedDelay = 1000 * 30, initialDelay = 1000 * 10)
    public void getUsers(){
        Iterable<UserDoc> users = elasticUserRepository.findAll();
        for(UserDoc u : users){
            System.out.println();
        }
    }

    @Scheduled(fixedDelay = 1000 * 30)
    public void saveUser(){
        if(user != null){
            UserDoc createdUserDoc = userService.registerNewUser(user);
            logger.info("user registered at: {}", new Date());
            user = null;
        }
    }
}
