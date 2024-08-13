package com.example.elastic.ElasticSearchTest.service;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.example.elastic.ElasticSearchTest.entity.User;
import com.example.elastic.ElasticSearchTest.payload.UserDto;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface UserService {
    UserDto registerNewUser(UserDto userDto);
    Iterable<UserDto> getAllUsers();
    UserDto getUserById(long id);
    UserDto updateUserDetails(long id, UserDto userDto);
    void deleteUser(long id);

    SearchResponse<User> searchUsers(String keyword) throws IOException;
    Map<String, Object> searchUserByName(String index, String name) throws IOException;
    Map<String, Object> searchUserByCity(String index, String city) throws IOException;
    List<Map<String, Object>> sortByField(String index, String field, String searchField, String searchValue, String sortOrder) throws IOException;
}
