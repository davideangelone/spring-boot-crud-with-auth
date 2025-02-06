package com.example.demo.logging;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class LoggingAspect {

  private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

  @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
  public void controllerMethods() {
  }

  @Before("controllerMethods()")
  public void logRequest(JoinPoint joinPoint) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    String methodName = signature.getName();
    String className = joinPoint.getTarget().getClass().getSimpleName();

    Object[] args = joinPoint.getArgs();
    Parameter[] parameters = signature.getMethod().getParameters();

    Map<String, Object> requestParams = new HashMap<>();

    for (int i = 0; i < args.length; i++) {
      if (parameters[i].isAnnotationPresent(RequestBody.class)) {
        requestParams.put("RequestBody", args[i]); // Capture @RequestBody
      } else if (parameters[i].isAnnotationPresent(RequestParam.class)) {
        requestParams.put(parameters[i].getName(), args[i]); // Capture @RequestParam
      } else if (args[i] instanceof HttpServletRequest request) {
        requestParams.put("HttpServletRequest", request.getRequestURI()); // Capture request URI
      }
    }

    logger.info("Request - {}.{}: {}", className, methodName, requestParams);
  }

  @AfterReturning(pointcut = "controllerMethods()", returning = "response")
  public void logResponse(JoinPoint joinPoint, Object response) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    String methodName = signature.getName();
    String className = joinPoint.getTarget().getClass().getSimpleName();

    if (response instanceof ResponseEntity) {
      logger.info("Response - {}.{}: Status={}, Body={}", className, methodName,
                  ((ResponseEntity<?>) response).getStatusCode(), ((ResponseEntity<?>) response).getBody());
    } else {
      logger.info("Response - {}.{}: {}", className, methodName, response);
    }
  }
}

