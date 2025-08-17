package com.example.lss.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserRegisterRequest {
    @NotBlank(message = "is required")
    @Pattern(regexp = "^[A-Za-z0-9._-]{3,32}$",
            message = "allowed: letters, digits, '.', '_' and '-', length 3–32")
    private String username;

    @NotBlank(message = "is required")
    @Size(min = 8, max = 128, message = "must be 8–128 characters")
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
