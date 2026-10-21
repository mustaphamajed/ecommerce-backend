package com.ecommerce.admin.role.exception;

import com.ecommerce.admin.common.exception.ConflictException;
import com.ecommerce.admin.role.domain.Permission;
import java.util.Set;

public class AdminLockoutException extends ConflictException {

    public AdminLockoutException(Set<Permission> missing) {
        super("ADMIN_LOCKOUT",
                "The ADMIN role must keep " + missing.stream().map(Permission::authority).sorted().toList()
                        + ", otherwise nobody could manage users and roles",
                "permissions");
    }
}
