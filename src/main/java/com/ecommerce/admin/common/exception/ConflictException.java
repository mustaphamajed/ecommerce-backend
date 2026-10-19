package com.ecommerce.admin.common.exception;

import org.springframework.http.HttpStatus;

public abstract class ConflictException extends ApiException {

    protected ConflictException(String code, String message) {
        super(code, message, null);
    }

    protected ConflictException(String code, String message, String property) {
        super(code, message, property);
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String title() {
        return "Conflict";
    }
}
