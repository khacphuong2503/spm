package com.javatechie.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.validation.annotation.Validated;


@Data
@Validated
public class UserInfoDTO {
    @NotEmpty(message = "Name is required")
    private String name;

    @NotEmpty(message = "Missing password")
    @Size(min = 8, message = "Password must be 8 characters or more")
    private String password;

    @NotEmpty(message = "Phone number is required")
    @Pattern(regexp = "^[+]?[0-9]{10,13}$", message = "Invalid phone number format")
    private String phoneNumber;

    @NotEmpty(message = "Missing Email")
    @Email(message = "Invalid email")
    private String email;
}