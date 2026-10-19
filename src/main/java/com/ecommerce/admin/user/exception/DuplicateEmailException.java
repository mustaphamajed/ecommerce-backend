package com.ecommerce.admin.user.exception;

import com.ecommerce.admin.common.exception.ConflictException;

public class DuplicateEmailException extends ConflictException {

    public DuplicateEmailException(String email) {
        super("EMAIL_TAKEN", "A user with email " + email + " already exists", "email");
    }
}
