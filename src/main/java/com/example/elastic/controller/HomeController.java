package com.example.elastic.controller;

import lombok.RequiredArgsConstructor;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HomeController {
    @Autowired
    private RestClient restClient;


    @GetMapping("/status")
    public String getStatus(){
        return "Running..";
    }







}
