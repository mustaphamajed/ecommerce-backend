package com.ecommerce.admin.role.exception;

import com.ecommerce.admin.common.exception.ConflictException;

public class DuplicateRoleNameException extends ConflictException {

    public DuplicateRoleNameException(String name) {
        super("ROLE_NAME_TAKEN", "A role named " + name + " already exists", "name");
    }
}
