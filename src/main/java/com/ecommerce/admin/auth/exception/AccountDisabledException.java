package com.ecommerce.admin.auth.exception;

import com.ecommerce.admin.common.exception.ForbiddenException;

public class AccountDisabledException extends ForbiddenException {

    public AccountDisabledException() {
        super("ACCOUNT_DISABLED", "This account is disabled");
    }
}
