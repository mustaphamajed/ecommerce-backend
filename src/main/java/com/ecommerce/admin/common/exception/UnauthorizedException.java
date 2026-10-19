package com.ecommerce.admin.common.exception;

import org.springframework.http.HttpStatus;

public abstract class UnauthorizedException extends ApiException {

    protected UnauthorizedException(String code, String message) {
        super(code, message, null);
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.UNAUTHORIZED;
    }

    @Override
    public String title() {
        return "Unauthorized";
    }
}
