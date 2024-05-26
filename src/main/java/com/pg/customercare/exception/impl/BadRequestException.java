package com.pg.customercare .exception.impl;

import com.pg.customercare .exception.WebException;
import org.springframework.http.HttpStatus;

public class BadRequestException extends WebException {

    public BadRequestException(final String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
