package com.ecommerce.admin.role.exception;

import com.ecommerce.admin.common.exception.ConflictException;

public class BuiltInRoleException extends ConflictException {

    private BuiltInRoleException(String message, String property) {
        super("ROLE_BUILT_IN", message, property);
    }

    public static BuiltInRoleException cannotRename(String name) {
        return new BuiltInRoleException("The built-in role " + name + " can't be renamed", "name");
    }

    public static BuiltInRoleException cannotDelete(String name) {
        return new BuiltInRoleException("The built-in role " + name + " can't be deleted", null);
    }
}
