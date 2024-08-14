package com.example.elastic.service;

import com.example.elastic.document.UserDoc;
import com.example.elastic.entity.UserEntity;
import com.example.elastic.repository.elastic.ElasticUserRepository;
import com.example.elastic.repository.mysql.MySqlUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DataSyncService {
    @Autowired
    private ElasticUserRepository elasticUserRepository;
    @Autowired
    private MySqlUserRepository mySqlUserRepository;

    public void syncData(){
        Iterable<UserDoc> userDocs = elasticUserRepository.findAll();
        List<UserDoc> elasticData = new ArrayList<>();
        userDocs.forEach(e -> elasticData.add(e));
        List<UserEntity> userEntities = elasticData.stream().map(e -> {
            UserEntity userEntity = new UserEntity();
            userEntity.setId(e.getId());
            userEntity.setName(e.getName());
            userEntity.setEmail(e.getEmail());
            userEntity.setCity(e.getCity());
            userEntity.setPassword(e.getPassword());
            return userEntity;
        }).collect(Collectors.toList());
        mySqlUserRepository.saveAll(userEntities);
    }
}
