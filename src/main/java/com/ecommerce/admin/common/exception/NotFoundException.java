package com.ecommerce.admin.common.exception;

import org.springframework.http.HttpStatus;

public abstract class NotFoundException extends ApiException {

    protected NotFoundException(String code, String message) {
        super(code, message, null);
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String title() {
        return "Resource not found";
    }
}
