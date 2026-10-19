package com.ecommerce.admin.common.exception;

import org.springframework.http.HttpStatus;

public abstract class BadRequestException extends ApiException {

    protected BadRequestException(String code, String message) {
        super(code, message, null);
    }

    protected BadRequestException(String code, String message, String property) {
        super(code, message, property);
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String title() {
        return "Bad request";
    }
}
