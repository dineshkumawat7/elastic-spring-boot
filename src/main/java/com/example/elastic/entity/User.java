package com.example.elastic.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;

@Document(indexName = "users")
@Data
public class User implements Serializable {
    @Id
    private long id;
    private String name;
    private String city;
    private int age;
    private double salary;
}
