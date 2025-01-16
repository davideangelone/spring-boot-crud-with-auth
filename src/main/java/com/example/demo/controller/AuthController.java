package com.example.demo.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.AuthenticationResponse;
import com.example.demo.model.LoginRequest;
import com.example.demo.model.RegisterRequest;
import com.example.demo.model.TokenRefreshRequest;
import com.example.demo.model.UserModel;
import com.example.demo.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
@EnableMethodSecurity(prePostEnabled = true)
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping(value = "/register",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Register a new user", description = "Registers a new user with a username and password")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Registration successful, JWT returned", content = {
                  @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                          schema = @Schema(implementation = AuthenticationResponse.class),
                          examples = @ExampleObject(name = "success", value = "{\"token\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\"}"))
          }),
          @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content()),
          @ApiResponse(responseCode = "409", description = "Username already exists", content = @Content())
  })
  public AuthenticationResponse register(@RequestBody @Valid RegisterRequest registerRequest) {
    return authService.register(registerRequest.getUsername(), registerRequest.getPassword());
  }

  @PostMapping(value = "/login",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Authenticate user", description = "Authenticates a user and returns a JWT token")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "UserEntity authenticated successfully, JWT returned", content = {
                  @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                          schema = @Schema(implementation = AuthenticationResponse.class),
                          examples = @ExampleObject(name = "success", value = "{\"token\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\"}"))
          }),
          @ApiResponse(responseCode = "401", description = "Authentication failed", content = @Content())
  })
  public AuthenticationResponse login(@RequestBody @Valid LoginRequest loginRequest) {
    return authService.login(loginRequest.getUsername(), loginRequest.getPassword());
  }

  @PostMapping(value = "/refresh",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Refresh access token", description = "Generate new access token from refresh token")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Access token refreshed successfully", content = {
                  @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                          schema = @Schema(implementation = AuthenticationResponse.class),
                          examples = @ExampleObject(name = "success", value = "{\"token\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\"}"))
          }),
          @ApiResponse(responseCode = "401", description = "Authentication failed", content = @Content())
  })
  public AuthenticationResponse refreshToken(@RequestBody TokenRefreshRequest request) {
    return authService.refresh(request.getRefreshToken());
  }

  @DeleteMapping(value = "/delete/{username}",
          produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Delete a user", description = "Delete a user by admin")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "204", description = "UserEntity deleted successfully", content = @Content()),
          @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content()),
          @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content()),
          @ApiResponse(responseCode = "404", description = "Username not found", content = @Content())
  })
  @PreAuthorize("hasRole('ADMIN')")
  public void delete(
          @Parameter(description = "Username of the user to be deleted", example = "john_doe")
          @PathVariable(value = "username") String username, Authentication authentication) {
    authService.delete(username, authentication);
  }


  @GetMapping(value = "/list",
          produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "List users", description = "List all users")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "List of users", content = @Content()),
          @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
  })
  @PreAuthorize("hasRole('ADMIN')")
  public List<UserModel> list() {
    return authService.list();
  }
}
