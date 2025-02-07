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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

  @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
  public void controllerMethods() {
  }

  @Before("controllerMethods()")
  public void logRequest(JoinPoint joinPoint) throws JsonProcessingException {
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

    LoggingBean loggingBean = new LoggingBean(className, methodName, "Request", requestParams);
    log.info(loggingBean.toString());
  }

  @AfterReturning(pointcut = "controllerMethods()", returning = "response")
  public void logResponse(JoinPoint joinPoint, Object response) throws JsonProcessingException {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    String methodName = signature.getName();
    String className = joinPoint.getTarget().getClass().getSimpleName();
    LoggingBean loggingBean = null;

    if (response instanceof ResponseEntity) {
      Map<String, Object> responseParams = new HashMap<>();
      responseParams.put("status", ((ResponseEntity<?>) response).getStatusCode());
      responseParams.put("Body", ((ResponseEntity<?>) response).getBody());
      loggingBean = new LoggingBean(className, methodName, "Response", responseParams);
    } else {
      loggingBean = new LoggingBean(className, methodName, "Response", response);
    }
    log.info(loggingBean.toString());
  }
}

