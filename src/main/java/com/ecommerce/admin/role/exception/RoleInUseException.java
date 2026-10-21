package com.ecommerce.admin.role.exception;

import com.ecommerce.admin.common.exception.ConflictException;

public class RoleInUseException extends ConflictException {

    public RoleInUseException(String name, long userCount) {
        super("ROLE_IN_USE",
                "The role " + name + " is assigned to " + userCount + " user(s), remove it from them first");
    }
}
