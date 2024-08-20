package com.example.elastic.payload;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserDto {
    @NotNull(message = "id is mandatory")
    @Positive(message = "id should be positive")
    private Long id;
    @NotEmpty(message = "name is mandatory")
    private String name;
    @Email(message = "invalid email")
    @NotNull(message = "email is mandatory")
    private String email;
    @NotEmpty(message = "city is mandatory")
    private String city;
    @NotNull(message = "password is mandatory")
    private String password;
}
