package com.example.elastic.ElasticSearchTest.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class ElasticSearchServiceImpl implements ElasticSearchService{
    @Autowired
    private RestClient restClient;
    @Autowired
    private ElasticsearchClient elasticsearchClient;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Map<String, String> getAllIndex(){
        Request request = new Request("GET", "/_cat/indices?format=json");
        try {
            Response response = restClient.performRequest(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            Map<String, String> index = new HashMap<>();
            for(JsonNode node : jsonNode){
                index.put(node.get("index").asText(), node.get("status").asText());
            }
            return index;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, Object> getIndexInformation(String index){
        Request request = new Request("GET", "/" + index);
        try {
            Response response = restClient.performRequest(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            return objectMapper.readValue(responseBody, Map.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String createIndex(String indexName, String indexSetting){
        Request request = new Request("PUT", "/" + indexName);
        request.setJsonEntity(indexSetting);
        boolean status = isIndexExists(indexName);
        if(!status){
            try {
                Response response = restClient.performRequest(request);
                return response.getEntity().getContent().toString();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else {
            throw new RuntimeException("'" + indexName + "' index already exists");
        }
    }

    @Override
    public boolean isIndexExists(String index){
        try {
            Request request = new Request("HEAD" ,"/" + index);
            Response response = restClient.performRequest(request);
            return response.getStatusLine().getStatusCode() == 200;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Map<String, Object>> getIndexData(String index) {

        String queryJson = "{\n" +
                "  \"query\": {\n" +
                "    \"match_all\": {\n" +
                "    }\n" +
                "  }\n" +
                "}";
        Request request = new Request("GET", "/" + index);
        request.setJsonEntity(queryJson);
        try {
            Response response = restClient.performRequest(request);
                System.out.println(response.getEntity());
                List<Map<String, Object>> responseData = Collections.singletonList(objectMapper.readValue(response.getEntity().getContent(), Map.class));
                return  responseData;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object matchQuery(String index ,String fieldName, String fieldValue) {
        String query = "{ \"query\": { \"match\": { \"" + fieldName + "\": { \"query\": \"" + fieldValue + "\", \"minimum_should_match\": \"1\" } } } }";
        try {
            Request request = new Request("POST",  "/" + index + "/" + "_search");
            request.setEntity(new StringEntity(query, ContentType.APPLICATION_JSON));
            Response response = restClient.performRequest(request);
            return objectMapper.readValue(response.getEntity().getContent(), Object.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
