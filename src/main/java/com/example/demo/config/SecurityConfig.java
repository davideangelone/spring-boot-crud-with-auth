package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.demo.security.JwtFilter;
import com.example.demo.security.JwtUtil;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JwtUtil jwtUtil;
  private final UserDetailsService userDetailsService;

  public SecurityConfig(JwtUtil jwtUtil,
                        UserDetailsService userDetailsService) {
    this.jwtUtil = jwtUtil;
    this.userDetailsService = userDetailsService;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)) // Allow H2 Console to be displayed in frames
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(authorize -> authorize
                                                    .requestMatchers(
                                                            "/api/auth/login",
                                                            "/api/auth/register",
                                                            "/api/auth/refresh",
                                                            "/swagger-ui.html",
                                                            "/v3/api-docs/**",
                                                            "/swagger-ui/**",
                                                            "/h2-console/**",
                                                            "/actuator/**",
                                                            "/error"
                                                    ).permitAll()
                                                    .anyRequest().authenticated()
        )
        .exceptionHandling(exceptionHandling -> exceptionHandling
                                                        .accessDeniedHandler((request, response, accessDeniedException) -> response.setStatus(HttpStatus.UNAUTHORIZED.value()))
                                                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
        )
        .addFilterBefore(new JwtFilter(jwtUtil, userDetailsService), UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
    return http.getSharedObject(AuthenticationManagerBuilder.class).build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
