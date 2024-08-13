package com.example.elastic.service;

import java.util.List;
import java.util.Map;

public interface ElasticSearchService {
    Map<String, String> getAllIndex();
    Map<String, Object> getIndexInformation(String index);
    String createIndex(String indexName, String indexSetting);
    boolean isIndexExists(String index);
    List<Map<String, Object>> getIndexData(String index);
    Object matchQuery(String index ,String fieldName, String fieldValue);
 }
