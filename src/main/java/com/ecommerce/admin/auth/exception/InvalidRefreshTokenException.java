package com.ecommerce.admin.auth.exception;

import com.ecommerce.admin.common.exception.UnauthorizedException;

public class InvalidRefreshTokenException extends UnauthorizedException {

    public InvalidRefreshTokenException() {
        super("INVALID_REFRESH_TOKEN", "The session has expired, please log in again");
    }
}
