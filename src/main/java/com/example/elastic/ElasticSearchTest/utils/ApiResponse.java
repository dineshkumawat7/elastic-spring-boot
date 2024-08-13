package com.example.elastic.ElasticSearchTest.utils;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private String message;
    private T data;
}
