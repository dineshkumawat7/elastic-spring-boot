package com.example.elastic.ElasticSearchTest.controller;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.example.elastic.ElasticSearchTest.entity.User;
import com.example.elastic.ElasticSearchTest.payload.UserDto;
import com.example.elastic.ElasticSearchTest.service.UserService;
import com.example.elastic.ElasticSearchTest.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> registerNewUser(@Valid @RequestBody UserDto userDto){
        ApiResponse<UserDto> response = new ApiResponse<>();
        try{
            UserDto u = userService.registerNewUser(userDto);
            response.setMessage("New user register successfully");
            response.setData(u);
            logger.info("new user register with id: " + u.getId());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }catch (Exception e){
            response.setMessage("" + e);
            response.setData(null);
            logger.error("" + e);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/get")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers(){
        ApiResponse<List<UserDto>> response = new ApiResponse<>();
        try{
            List<UserDto> users = (List<UserDto>) userService.getAllUsers();
           response.setData(users);
           response.setMessage("All users");
           return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e){
            response.setMessage("" + e);
            response.setData(null);
            logger.error("" + e);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUser(@PathVariable(value = "id") long id){
        ApiResponse<UserDto> response = new ApiResponse<>();
        try{
            UserDto user = userService.getUserById(id);
            response.setData(user);
            response.setMessage("User found with id: " + id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e){
            response.setMessage("" + e);
            response.setData(null);
            logger.error("" + e);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(@PathVariable(value = "id", required = true) long id,
                                                        @Valid @RequestBody UserDto userDto){
        ApiResponse<UserDto> response = new ApiResponse<>();
        try{
            UserDto updatedUser = userService.updateUserDetails(id, userDto);
            response.setData(updatedUser);
            response.setMessage("User details updated");
            logger.info("update user details with id: " + updatedUser.getId());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e){
            response.setMessage("" + e);
            response.setData(null);
            logger.error("" + e);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<UserDto>> deleteUser(@PathVariable("id") long id){
        ApiResponse<UserDto> response = new ApiResponse<>();
        try{
            userService.deleteUser(id);
            response.setData(null);
            response.setMessage("Successfully deleted user account with id: " + id);
            logger.info("delete user account with id: {}", id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e){
            response.setMessage("" + e);
            response.setData(null);
            logger.error("" + e);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/s")
    public ResponseEntity<ApiResponse<SearchResponse<User>>> searchUsers(@PathVariable String keyword) throws IOException {
        SearchResponse<User> users = userService.searchUsers(keyword);
        ApiResponse<SearchResponse<User>> response = new ApiResponse<>();
        response.setMessage("search result..");
        response.setData(users);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/searchByName")
    public Map<String, Object> searchUserByName(@RequestParam String name) throws IOException {
        return userService.searchUserByName("users", name);
    }

    @GetMapping("/searchByCity")
    public ResponseEntity<ApiResponse<Map<String, Object>>> searchUserByCity(@RequestParam String index ,@RequestParam String city){
        ApiResponse<Map<String, Object>> response = new ApiResponse<>();
        try{
            Map<String, Object> users = userService.searchUserByCity(index, city);
            response.setMessage("search result by city..");
            response.setData(users);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e){
            response.setMessage("Something wrong on server..");
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/sort")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> sortUsers(
            @RequestParam String field,
            @RequestParam String searchField,
            @RequestParam String searchValue,
            @RequestParam String sortOrder
            ){
        ApiResponse<List<Map<String, Object>>> response = new ApiResponse<>();
        try{
            List<Map<String, Object>> users = userService.sortByField("users" , field, searchField, searchValue, sortOrder);
            response.setMessage("sorted result..");
            response.setData(users);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e){
            logger.error("" + e);
            response.setMessage("Something wrong on server..");
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
