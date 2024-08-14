package com.example.elastic.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;

@Document(indexName = "users")
@Data
public class UserDoc implements Serializable {
    @Id
    private long id;
    private String name;
    private String email;
    private String city;
    private String password;
}
