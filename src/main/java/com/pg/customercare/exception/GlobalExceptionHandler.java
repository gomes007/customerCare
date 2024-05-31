package com.pg.customercare.exception;

import com.pg.customercare.exception.impl.*;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleNotFoundException(
    NotFoundException ex
  ) {
    return new ResponseEntity<>(
      Map.of(
        "message",
        ex.getMessage(),
        "status",
        HttpStatus.NOT_FOUND.value()
      ),
      HttpStatus.NOT_FOUND
    );
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<Map<String, Object>> handleBadRequestException(
    BadRequestException ex
  ) {
    return new ResponseEntity<>(
      Map.of(
        "message",
        ex.getMessage(),
        "status",
        HttpStatus.BAD_REQUEST.value()
      ),
      HttpStatus.BAD_REQUEST
    );
  }

  @ExceptionHandler(InternalServerException.class)
  public ResponseEntity<Map<String, Object>> handleInternalServerException(
    InternalServerException ex
  ) {
    return new ResponseEntity<>(
      Map.of(
        "message",
        ex.getMessage(),
        "status",
        HttpStatus.INTERNAL_SERVER_ERROR.value()
      ),
      HttpStatus.INTERNAL_SERVER_ERROR
    );
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<Map<String, Object>> handleValidationException(
    ValidationException ex
  ) {
    Map<String, Object> errorDetails = Map.of(
      "message",
      ex.getMessage(),
      "errors",
      ex.getErrorRef(),
      "status",
      HttpStatus.BAD_REQUEST.value()
    );
    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(
    MethodArgumentNotValidException ex
  ) {
    Map<String, String> errors = new HashMap<>();
    ex
      .getBindingResult()
      .getAllErrors()
      .forEach(error -> {
        String fieldName = ((FieldError) error).getField();
        String errorMessage = error.getDefaultMessage();
        errors.put(fieldName, errorMessage);
      });
    Map<String, Object> errorDetails = Map.of(
      "message",
      "Validation failed",
      "errors",
      errors,
      "status",
      HttpStatus.BAD_REQUEST.value()
    );
    throw new BeanValidationException("Validation failed", errorDetails);
  }

  @ExceptionHandler(WebException.class)
  public ResponseEntity<Map<String, Object>> handleWebException(
    WebException ex
  ) {
    return new ResponseEntity<>(
      Map.of("message", ex.getMessage(), "status", ex.getStatus().value()),
      ex.getStatus()
    );
  }
}
