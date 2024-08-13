package com.example.elastic.ElasticSearchTest.repository;

import com.example.elastic.ElasticSearchTest.entity.User;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends ElasticsearchRepository<User, Long> {

}
