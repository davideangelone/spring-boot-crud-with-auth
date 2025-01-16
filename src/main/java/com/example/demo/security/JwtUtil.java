package com.example.demo.security;

import java.security.Key;
import java.util.Collection;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtil {

  @Value("${jwt.accessToken.secret}")
  private String jwtAccessTokenSecret;

  @Value("${jwt.accessToken.expiration}")
  private long jwtAccessTokenExpiration;

  @Value("${jwt.refreshToken.secret}")
  private String jwtRefreshTokenSecret;

  @Value("${jwt.refreshToken.expiration}")
  private long jwtRefreshTokenExpiration;


  private SecretKey accessTokenKey;
  private SecretKey refreshTokenKey;

  @PostConstruct
  private void init() {
    accessTokenKey = new SecretKeySpec(Sha512DigestUtils.sha(jwtAccessTokenSecret), SignatureAlgorithm.HS256.getJcaName());
    refreshTokenKey = new SecretKeySpec(Sha512DigestUtils.sha(jwtRefreshTokenSecret), SignatureAlgorithm.HS256.getJcaName());
  }

  public String generateAccessToken(String username, Collection<? extends GrantedAuthority> authorities) {
    return Jwts.builder()
               .setSubject(username)
               .setIssuedAt(new Date())
               .claim("authorities", authorities)
               .setExpiration(new Date(System.currentTimeMillis() + jwtAccessTokenExpiration))
               .signWith(accessTokenKey)
               .compact();
  }

  public String generateRefreshToken(String username) {
    return Jwts.builder()
               .setSubject(username)
               .setIssuedAt(new Date())
               .setExpiration(new Date(System.currentTimeMillis() + jwtRefreshTokenExpiration))
               .signWith(refreshTokenKey)
               .compact();
  }

  public boolean validateAccessToken(String accessToken) {
    if (null == accessToken) {
      return false;
    }
    try {
      parseToken(accessToken, accessTokenKey);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  public boolean validateRefreshToken(String refreshToken) {
    if (null == refreshToken) {
      return false;
    }
    try {
      parseToken(refreshToken, refreshTokenKey);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  private Jws<Claims> parseToken(String token, Key key) {
    return Jwts.parserBuilder()
               .setSigningKey(key)
               .build()
               .parseClaimsJws(token);
  }

  public String getUsernameFromAccessToken(String accessToken) {
    return parseToken(accessToken, accessTokenKey).getBody().getSubject();
  }

  public String getUsernameFromRefreshToken(String refreshToken) {
    return parseToken(refreshToken, refreshTokenKey).getBody().getSubject();
  }

  public String extractToken(HttpServletRequest request) {
    final String authorizationHeader = request.getHeader("Authorization");
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      return authorizationHeader.substring(7);
    }
    return null;
  }

}