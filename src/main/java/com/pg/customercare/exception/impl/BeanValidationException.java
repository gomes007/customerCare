package com.pg.customercare.exception.impl;

import com.pg.customercare.exception.WebException;
import java.util.Map;
import org.springframework.http.HttpStatus;

public class BeanValidationException extends WebException {
    public BeanValidationException(final String message, Map<String, Object> errorRef) {
        super(message, HttpStatus.BAD_REQUEST, errorRef);
    }
}
