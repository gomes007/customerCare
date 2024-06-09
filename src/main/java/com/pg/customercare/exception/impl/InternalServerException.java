package com.pg.customercare.exception.impl;

import com.pg.customercare.exception.WebException;
import org.springframework.http.HttpStatus;

public class InternalServerException extends WebException {
  public InternalServerException(final String message) {
    super(message, HttpStatus.INTERNAL_SERVER_ERROR);
  }
    
  public InternalServerException(final String message, Throwable cause) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }
}
