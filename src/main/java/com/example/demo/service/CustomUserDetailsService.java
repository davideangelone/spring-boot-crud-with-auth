package com.example.demo.service;

import com.example.demo.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository.findByUsername(username)
                         .map(user -> User.builder()
                                          .username(user.getUsername())
                                          .password(user.getPassword())
                                          .roles(user.getRole().name())
                                          .build())
                         .orElseThrow(() -> new UsernameNotFoundException("UserEntity not found"));
  }

}
