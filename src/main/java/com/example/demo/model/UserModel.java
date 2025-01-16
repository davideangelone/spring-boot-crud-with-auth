package com.example.demo.model;

import com.example.demo.constants.UserRole;

import lombok.Data;

@Data
public class UserModel {
  private Long id;
  private String username;
  private UserRole role;
}
