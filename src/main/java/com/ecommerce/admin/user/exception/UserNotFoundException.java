package com.ecommerce.admin.user.exception;

import com.ecommerce.admin.common.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException(Long id) {
        super("USER_NOT_FOUND", "User " + id + " not found");
    }
}
