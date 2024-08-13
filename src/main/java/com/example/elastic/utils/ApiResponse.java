package com.example.elastic.utils;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private String message;
    private T data;
}
