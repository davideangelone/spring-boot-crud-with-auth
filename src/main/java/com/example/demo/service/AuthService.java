package com.example.demo.service;

import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.constants.UserRole;
import com.example.demo.entity.UserEntity;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.UsernameAlreadyExistsException;
import com.example.demo.mapper.EntityMapper;
import com.example.demo.model.AuthenticationResponse;
import com.example.demo.model.UserModel;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;

import jakarta.transaction.Transactional;

@Service
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  private final UserDetailsService userDetailsService;

  public AuthService(AuthenticationManager authenticationManager,
                     UserRepository userRepository,
                     BCryptPasswordEncoder passwordEncoder,
                     JwtUtil jwtUtil,
                     UserDetailsService userDetailsService) {
    this.authenticationManager = authenticationManager;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
    this.userDetailsService = userDetailsService;
  }

  public AuthenticationResponse login(String username, String password) {
    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password));
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    String accessToken = jwtUtil.generateAccessToken(userDetails.getUsername(), userDetails.getAuthorities());
    String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());
    return new AuthenticationResponse(accessToken, refreshToken);
  }

  public AuthenticationResponse register(String username, String password) {
    if (userRepository.existsByUsername(username)) {
      throw new UsernameAlreadyExistsException("Username already exists");
    }
    UserEntity userEntity = new UserEntity();
    userEntity.setUsername(username);
    userEntity.setPassword(passwordEncoder.encode(password));
    userEntity.setRole(UserRole.USER);
    userRepository.save(userEntity);

    return login(username, password);
  }

  public AuthenticationResponse refresh(String refreshToken) {
    if (!jwtUtil.validateRefreshToken(refreshToken)) {
      throw new BadCredentialsException("Invalid refresh token");
    }

    String username = jwtUtil.getUsernameFromRefreshToken(refreshToken);
    if (!userRepository.existsByUsername(username)) {
      throw new BadCredentialsException("Invalid refresh token");
    }
    
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    String accessToken = jwtUtil.generateAccessToken(username, userDetails.getAuthorities());
    String newRefreshToken = jwtUtil.generateRefreshToken(username);
    return new AuthenticationResponse(accessToken, newRefreshToken);
  }

  @Transactional
  public void delete(String username, Authentication authentication) {
    if (!userRepository.existsByUsername(username)) {
      throw new UsernameNotFoundException("Username not found");
    }
    UsernamePasswordAuthenticationToken principalToken = (UsernamePasswordAuthenticationToken) authentication;
    String currentUsername = ((User) principalToken.getPrincipal()).getUsername();
    if (username.equals(currentUsername)) {
      throw new BadRequestException("Cannot delete yourself");
    }
    userRepository.deleteByUsername(username);
  }

  public List<UserModel> list() {
    return userRepository.findAll().stream()
                         .map(EntityMapper.INSTANCE::toUserModel)
                         .toList();
  }

}
