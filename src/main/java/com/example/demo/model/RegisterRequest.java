package com.example.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@Schema(description = "Model representing user registration details")
public class RegisterRequest {
  @Schema(description = "Username of the user", example = "john_doe")
  @NotEmpty(message = "Username cannot be empty")
  private String username;

  @Schema(description = "Password of the user", example = "password123")
  @NotEmpty(message = "Password cannot be empty")
  private String password;
}
