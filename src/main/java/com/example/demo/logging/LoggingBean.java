package com.example.demo.logging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
public class LoggingBean {
  private String className;
  private String methodName;
  private String type;
  private Object object;
}
