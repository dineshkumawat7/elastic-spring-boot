package com.example.elastic.controller;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.example.elastic.document.UserDoc;
import com.example.elastic.payload.UserDto;
import com.example.elastic.service.UserScheduler;
import com.example.elastic.service.UserService;
import com.example.elastic.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserScheduler userScheduler;

    @PostMapping("/s/register")
    public ResponseEntity<?> registerNewUserWithScheduler(@Valid @RequestBody UserDto userDto) {
        ApiResponse<UserDto> response = new ApiResponse<>();
        try {
            userScheduler.registerUser(userDto);
            response.setMessage("New user register successfully");
            response.setData(userDto);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setData(null);
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDoc>> registerNewUser(@Valid @RequestBody UserDto userDto) {
        ApiResponse<UserDoc> response = new ApiResponse<>();
        try {
            UserDoc u = userService.registerNewUser(userDto);
            response.setMessage("New user register successfully");
            response.setData(u);
            log.info("new user register with id: {}", u.getId());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.setMessage("" + e);
            response.setData(null);
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/get")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        ApiResponse<List<UserDto>> response = new ApiResponse<>();
        try {
            List<UserDto> users = (List<UserDto>) userService.getAllUsers();
            response.setData(users);
            response.setMessage("All users");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setMessage("" + e);
            response.setData(null);
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUser(@PathVariable(value = "id") long id) {
        ApiResponse<UserDto> response = new ApiResponse<>();
        try {
            UserDto user = userService.getUserById(id);
            response.setData(user);
            response.setMessage("User found with id: " + id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setMessage("" + e);
            response.setData(null);
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(@PathVariable(value = "id", required = true) long id,
                                                           @Valid @RequestBody UserDto userDto) {
        ApiResponse<UserDto> response = new ApiResponse<>();
        try {
            UserDto updatedUser = userService.updateUserDetails(id, userDto);
            response.setData(updatedUser);
            response.setMessage("User details updated");
            log.info("update user details with id: {}", updatedUser.getId());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setMessage("" + e);
            response.setData(null);
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<UserDto>> deleteUser(@PathVariable("id") long id) {
        ApiResponse<UserDto> response = new ApiResponse<>();
        try {
            userService.deleteUser(id);
            response.setData(null);
            response.setMessage("Successfully deleted user account with id: " + id);
            log.info("delete user account with id: {}", id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setMessage("" + e);
            response.setData(null);
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/s")
    public ResponseEntity<ApiResponse<SearchResponse<UserDoc>>> searchUsers(@PathVariable String keyword) throws IOException {
        SearchResponse<UserDoc> users = userService.searchUsers(keyword);
        ApiResponse<SearchResponse<UserDoc>> response = new ApiResponse<>();
        response.setMessage("search result..");
        response.setData(users);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/searchByName")
    public Map<String, Object> searchUserByName(@RequestParam String name) throws IOException {
        return userService.searchUserByName("users", name);
    }

    @GetMapping("/searchByCity")
    public ResponseEntity<ApiResponse<Map<String, Object>>> searchUserByCity(@RequestParam String index, @RequestParam String city) {
        ApiResponse<Map<String, Object>> response = new ApiResponse<>();
        try {
            Map<String, Object> users = userService.searchUserByCity(index, city);
            response.setMessage("search result by city..");
            response.setData(users);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setMessage(e.getMessage());
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
    ) {
        ApiResponse<List<Map<String, Object>>> response = new ApiResponse<>();
        try {
            List<Map<String, Object>> users = userService.sortByField("users", field, searchField, searchValue, sortOrder);
            response.setMessage("sorted result..");
            response.setData(users);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            response.setMessage("Something wrong on server..");
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
