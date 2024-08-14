package com.example.elastic.service;

import com.example.elastic.document.UserDoc;
import com.example.elastic.entity.UserEntity;
import com.example.elastic.repository.elastic.ElasticUserRepository;
import com.example.elastic.repository.mysql.MySqlUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DataSyncService {
    @Autowired
    private ElasticUserRepository elasticUserRepository;
    @Autowired
    private MySqlUserRepository mySqlUserRepository;

    public void syncData() {
        List<UserDoc> users = elasticUserRepository.findAllUsers();
        List<UserEntity> userEntities = users.stream().map(e -> {
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

    public Map<Long, String> transformMySqlDataToMap(List<UserEntity> userEntities) {
        return userEntities.stream().collect(Collectors.toMap(UserEntity::getId, UserEntity::getEmail));
    }

    public Map<Long, String> transformESDataToMap(List<UserDoc> userDocs) {
        return userDocs.stream().collect(Collectors.toMap(UserDoc::getId, UserDoc::getEmail));
    }

    public boolean isEqualUserData() {
        List<UserDoc> elasticData = elasticUserRepository.findAllUsers();
        List<UserEntity> mysqlData = mySqlUserRepository.findAll();

        Map<Long, String> allESUsers = transformESDataToMap(elasticData);
        Map<Long, String> allMySqlUsers = transformMySqlDataToMap(mysqlData);
        return !allESUsers.equals(allMySqlUsers);
    }
}
