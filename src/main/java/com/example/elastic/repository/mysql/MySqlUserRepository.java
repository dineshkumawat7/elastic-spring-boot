package com.example.elastic.repository.mysql;

import com.example.elastic.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MySqlUserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);
}
