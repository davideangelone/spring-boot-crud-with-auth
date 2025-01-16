package com.example.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Model to refresh access token")
public class TokenRefreshRequest {
  @Schema(description = "Refresh token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
  private String refreshToken;
}
