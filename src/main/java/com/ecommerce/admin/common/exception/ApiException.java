package com.ecommerce.admin.common.exception;

import org.springframework.http.HttpStatus;

public abstract class ApiException extends RuntimeException {

    private final String code;
    private final String property;

    protected ApiException(String code, String message, String property) {
        super(message);
        this.code = code;
        this.property = property;
    }

    public abstract HttpStatus status();

    public abstract String title();

    public String code() {
        return code;
    }

    public String property() {
        return property;
    }
}
