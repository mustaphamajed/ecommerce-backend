package com.ecommerce.admin.role.exception;

import com.ecommerce.admin.common.exception.NotFoundException;

public class RoleNotFoundException extends NotFoundException {

    public RoleNotFoundException(Long id) {
        super("ROLE_NOT_FOUND", "Role " + id + " not found");
    }
}
