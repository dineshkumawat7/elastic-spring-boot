package com.example.elastic.ElasticSearchTest.controller;

import com.example.elastic.ElasticSearchTest.service.ElasticSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class ElasticSearchController {
    @Autowired
    private ElasticSearchService elasticSearchService;

    @GetMapping("/getAllIndex")
    public ResponseEntity<Map<String, String>> getAllIndex(){
          Map<String, String> indexes =  elasticSearchService.getAllIndex();
          return ResponseEntity.status(HttpStatus.OK).body(indexes);
    }

    @GetMapping("/getIndexInfo")
    public ResponseEntity<Map<String, Object>> getIndexInformation(@RequestParam String index){
     try{
          Map<String, Object> indexInfo = elasticSearchService.getIndexInformation(index);
          return ResponseEntity.status(HttpStatus.OK).body(indexInfo);
     }catch (Exception e){
         e.printStackTrace();
         return null;
     }
    }

    @GetMapping("/indexExists")
    public ResponseEntity<String> isIndexExist(@RequestParam String index){
        try{
            boolean status = elasticSearchService.isIndexExists(index);
            if(status){
                return ResponseEntity.status(HttpStatus.OK).body("Index is available");
            }else {
                return ResponseEntity.status(HttpStatus.OK).body("Index is not available");
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something wrong on server");
        }
    }

    @PostMapping("/createNewIndex")
    public ResponseEntity<String> createNewIndex(
            @RequestParam String index,
            @RequestParam String indexSetting){
        try{
            String createdIndex = elasticSearchService.createIndex(index,indexSetting);
            return new ResponseEntity<>(createdIndex, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("" + e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getIndex")
    public ResponseEntity<List<Map<String, Object>>> getIndexData(@RequestParam String index){
           List<Map<String, Object>> indexData = elasticSearchService.getIndexData(index);
           return new ResponseEntity<>(indexData, HttpStatus.OK);
    }

    @GetMapping("/matchQuery")
    public ResponseEntity<Object> matchQuery(@RequestParam String index, @RequestParam String fieldName, @RequestParam String fieldValue){
        try{
            Object result = elasticSearchService.matchQuery(index, fieldName, fieldValue);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error performing match query");
        }
    }
}
