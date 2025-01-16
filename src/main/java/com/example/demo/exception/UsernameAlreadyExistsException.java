package com.example.demo.exception;

public class UsernameAlreadyExistsException extends RuntimeException {
  public UsernameAlreadyExistsException(String msg) {
    super(msg);
  }

  public UsernameAlreadyExistsException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
