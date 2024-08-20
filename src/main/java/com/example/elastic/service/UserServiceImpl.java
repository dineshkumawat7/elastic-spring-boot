package com.example.elastic.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import com.example.elastic.document.UserDoc;
import com.example.elastic.exception.UserNotFoundException;
import com.example.elastic.payload.UserDto;
import com.example.elastic.repository.elastic.ElasticUserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private ElasticUserRepository elasticUserRepository;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private ElasticsearchClient elasticsearchClient;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RestClient restClient;

    public UserDoc registerNewUser(UserDto userDto) {
        UserDoc userDoc = new UserDoc();
        userDoc.setId(userDto.getId());
        userDoc.setName(userDto.getName());
        userDoc.setCity(userDto.getCity());
        userDoc.setEmail(userDto.getEmail());
        userDoc.setPassword(userDto.getPassword());
        UserDoc createdUser = elasticUserRepository.save(userDoc);
        return mapper.map(createdUser, UserDoc.class);
    }

    public List<UserDto> getAllUsers() {
        Iterable<UserDoc> iterable = elasticUserRepository.findAll();
        List<UserDoc> userDocs = new ArrayList<>();
        for (UserDoc userDoc : iterable) {
            userDocs.add(userDoc);
        }
        return userDocs.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private UserDto convertToDto(UserDoc userDoc) {
        return mapper.map(userDoc, UserDto.class);
    }

    public UserDto getUserById(long id) {
        UserDoc userDoc = this.elasticUserRepository.findById(id).orElseThrow();
        return mapper.map(userDoc, UserDto.class);
    }

    public UserDto updateUserDetails(long id, UserDto userDto) {
        Optional<UserDoc> existingUser = this.elasticUserRepository.findById(id);
        if (existingUser.isPresent()) {
            UserDoc u = existingUser.get();
            u.setId(userDto.getId());
            u.setName(userDto.getName());
            u.setCity(userDto.getCity());
            u.setEmail(userDto.getEmail());
            u.setPassword(userDto.getPassword());
            elasticUserRepository.save(u);
            return mapper.map(u, UserDto.class);
        } else {
            throw new UserNotFoundException("User not found with id: " + id);
        }
    }

    public void deleteUser(long id) {
        Optional<UserDoc> user = this.elasticUserRepository.findById(id);
        if (user.isPresent()) {
            this.elasticUserRepository.deleteById(id);
        } else {
            throw new UserNotFoundException("User not found with id: " + id);
        }
    }

    @Override
    public SearchResponse<UserDoc> searchUsers(String keyword) throws IOException {
        SearchResponse<UserDoc> response = elasticsearchClient.search(s -> s.index("users").query(q -> q.match(t -> t.field("name").query(keyword))), UserDoc.class);
        TotalHits total = response.hits().total();
        boolean isExactResult = total.relation() == TotalHitsRelation.Eq;

        if (isExactResult) {
            logger.info("There are {} results", total.value());
        } else {
            logger.info("There are more than {} results", total.value());
        }

        List<Hit<UserDoc>> hits = response.hits().hits();
        for (Hit<UserDoc> hit : hits) {
            UserDoc userDoc = hit.source();
            logger.info("Found user {}, score {}", userDoc.getName(), hit.score());
        }

        return response;
    }


    public Map<String, Object> searchUserByName(String index, String name) throws IOException {
        // Create a search query
        String query = "{\n" + "  \"query\": {\n" + "    \"match\": {\n" + "      \"name\": \"" + name + "\"\n" + "    }\n" + "  }\n" + "}";

        // Create a search request
        Request request = new Request("GET", "/" + index + "/_search");
        request.setJsonEntity(query);

        // Perform the request and get the response
        Response response = restClient.performRequest(request);

        // Parse the response and convert it to a Map
        Map<String, Object> responseBody = objectMapper.readValue(response.getEntity().getContent(), HashMap.class);
        return responseBody;
    }

    public Map<String, Object> searchUserByCity(String index, String city) throws IOException {
        String query = "{\n" + "  \"query\": {\n" + "    \"match\": {\n" + "      \"city\": \"" + city + "\"\n" + "    }\n" + "  }\n" + "}";

        Request request = new Request("GET", "/" + index + "/_search");
        request.setJsonEntity(query);

        Response response = restClient.performRequest(request);
        Map<String, Object> responseBody = objectMapper.readValue(response.getEntity().getContent(), HashMap.class);
        return responseBody;
    }

    public List<Map<String, Object>> sortByField(String index, String field, String searchField, String searchValue, String sortOrder) throws IOException {
        String query = "{\n" + "  \"query\": {\n" + "    \"match\": { \"" + searchField + "\": \"" + searchValue + "\" }\n" + "  },\n" + "  \"sort\": [\n" + "    { \"" + field + "\": { \"order\": \"" + sortOrder + "\" } }\n" + "  ]\n" + "}";

        Request request = new Request("GET", "/" + index + "/_search");
        request.setJsonEntity(query);
        Response response = restClient.performRequest(request);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(response.getEntity().getContent());
        List<Map<String, Object>> results = new ArrayList<>();
        jsonNode.path("hits").path("hits").forEach(hit -> {
            results.add(mapper.convertValue(hit.path("_source"), Map.class));
        });
        return results;
    }
}
