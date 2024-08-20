package com.example.elastic.repository.elastic;

import com.example.elastic.document.UserDoc;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElasticUserRepository extends ElasticsearchRepository<UserDoc, Long> {
    @Query("{ \"match_all\": {} }")
    List<UserDoc> findAllUsers();
    UserDoc findByEmail(String email);
}
