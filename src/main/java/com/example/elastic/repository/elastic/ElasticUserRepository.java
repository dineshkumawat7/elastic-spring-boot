package com.example.elastic.repository.elastic;

import com.example.elastic.document.UserDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticUserRepository extends ElasticsearchRepository<UserDoc, Long> {
    UserDoc findByEmail(String email);
}
