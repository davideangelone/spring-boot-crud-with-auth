package com.example.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@Schema(description = "Model representing user login credentials")
public class LoginRequest {
  @Schema(description = "Username of the user", example = "admin")
  @NotEmpty(message = "Username cannot be empty")
  private String username;

  @Schema(description = "Password of the user", example = "admin")
  @NotEmpty(message = "Password cannot be empty")
  private String password;
}
