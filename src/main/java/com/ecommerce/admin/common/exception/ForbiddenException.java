package com.ecommerce.admin.common.exception;

import org.springframework.http.HttpStatus;

public abstract class ForbiddenException extends ApiException {

    protected ForbiddenException(String code, String message) {
        super(code, message, null);
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.FORBIDDEN;
    }

    @Override
    public String title() {
        return "Forbidden";
    }
}
