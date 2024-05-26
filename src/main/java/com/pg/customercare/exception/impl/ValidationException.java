package com.pg.customercare.exception.impl;

import com.pg.customercare.exception.WebException;
import org.springframework.http.HttpStatus;
import java.util.Map;

public class ValidationException extends WebException {
    public ValidationException(final String message, Map<String, Object> errorRef) {
        super(message, HttpStatus.BAD_REQUEST, errorRef);
    }
}
