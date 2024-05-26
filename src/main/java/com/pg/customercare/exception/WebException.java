package com.pg.customercare.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;

public abstract class WebException extends RuntimeException {
    private final HttpStatus status;
    private final Map<String, Object> errorRef;

    public WebException(final String message, final HttpStatus status) {
        super(message);
        this.status = status;
        this.errorRef = null;
    }

    public WebException(final String message, final HttpStatus status, Map<String, Object> errorRef) {
        super(message);
        this.status = status;
        this.errorRef = errorRef;
    }

    public HttpStatus getStatus() {
        return this.status;
    }

    public Map<String, Object> getErrorRef() {
        return this.errorRef;
    }
}
