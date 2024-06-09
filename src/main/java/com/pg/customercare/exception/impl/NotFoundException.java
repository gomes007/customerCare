package com.pg.customercare.exception.impl;

import com.pg.customercare.exception.WebException;
import org.springframework.http.HttpStatus;

public class NotFoundException extends WebException {
    public NotFoundException(final String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
