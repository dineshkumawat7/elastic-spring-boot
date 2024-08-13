package com.example.elastic.ElasticSearchTest.utils;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ValidationErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private Map<String, String> message;
}
