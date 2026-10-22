package com.ecommerce.admin.user.auth.exception;

import com.ecommerce.admin.common.exception.UnauthorizedException;

public class InvalidCredentialsException extends UnauthorizedException {

    public InvalidCredentialsException() {
        super("INVALID_CREDENTIALS", "Invalid email or password");
    }
}
