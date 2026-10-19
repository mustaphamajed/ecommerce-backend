package com.ecommerce.admin.user.exception;

import com.ecommerce.admin.common.exception.ConflictException;

public class LastAdminException extends ConflictException {

    public LastAdminException() {
        super("LAST_ADMIN", "At least one enabled administrator is required");
    }
}
