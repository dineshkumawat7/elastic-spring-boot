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
    @NotEmpty(message = "city is mandatory")
    private String city;
    @NotNull(message = "Age is mandatory")
    @Min(value = 1, message = "Age should not be less than 1")
    @Max(value = 100, message = "Age should not be more than 100")
    private Integer age;
    @NotNull(message = "salary is mandatory")
    private Double salary;
}
