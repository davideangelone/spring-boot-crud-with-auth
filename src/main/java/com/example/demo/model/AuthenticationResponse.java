package com.example.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Model representing success response")
public class AuthenticationResponse {
  @Schema(description = "Generated JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
  private String accessToken;

  @Schema(description = "Generated JWT refresh token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
  private String refreshToken;
}
