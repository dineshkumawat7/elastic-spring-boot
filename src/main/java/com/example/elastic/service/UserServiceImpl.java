package com.example.elastic.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import com.example.elastic.entity.User;
import com.example.elastic.exception.UserNotFoundException;
import com.example.elastic.payload.UserDto;
import com.example.elastic.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private ElasticsearchClient elasticsearchClient;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RestClient restClient;

    public User registerNewUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setCity(userDto.getCity());
        user.setAge(userDto.getAge());
        user.setSalary(userDto.getSalary());
        userRepository.save(user);
        return mapper.map(user, User.class);
    }

    public List<UserDto> getAllUsers() {
        Iterable<User> iterable = userRepository.findAll();
        List<User> users = new ArrayList<>();
        for (User user : iterable) {
            users.add(user);
        }
        return users.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private UserDto convertToDto(User user) {
        return mapper.map(user, UserDto.class);
    }

    public UserDto getUserById(long id) {
        User user = this.userRepository.findById(id).orElseThrow();
        return mapper.map(user, UserDto.class);
    }

    public UserDto updateUserDetails(long id, UserDto userDto) {
        Optional<User> existingUser = this.userRepository.findById(id);
        if (existingUser.isPresent()) {
            User u = existingUser.get();
            u.setId(userDto.getId());
            u.setName(userDto.getName());
            u.setCity(userDto.getCity());
            u.setAge(userDto.getAge());
            u.setSalary(userDto.getSalary());
            userRepository.save(u);
            return mapper.map(u, UserDto.class);
        } else {
            throw new UserNotFoundException("User not found with id: " + id);
        }
    }

    public void deleteUser(long id) {
        Optional<User> user = this.userRepository.findById(id);
        if (user.isPresent()) {
            this.userRepository.deleteById(id);
        } else {
            throw new UserNotFoundException("User not found with id: " + id);
        }
    }

    @Override
    public SearchResponse<User> searchUsers(String keyword) throws IOException {
        SearchResponse<User> response = elasticsearchClient.search(s -> s.index("users").query(q -> q.match(t -> t.field("name").query(keyword))), User.class);
        TotalHits total = response.hits().total();
        boolean isExactResult = total.relation() == TotalHitsRelation.Eq;

        if (isExactResult) {
            logger.info("There are " + total.value() + " results");
        } else {
            logger.info("There are more than " + total.value() + " results");
        }

        List<Hit<User>> hits = response.hits().hits();
        for (Hit<User> hit : hits) {
            User user = hit.source();
            logger.info("Found user " + user.getName() + ", score " + hit.score());
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
