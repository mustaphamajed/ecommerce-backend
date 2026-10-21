package com.ecommerce.admin.role.exception;

import com.ecommerce.admin.common.exception.BadRequestException;
import java.util.Set;

public class UnknownPermissionException extends BadRequestException {

    public UnknownPermissionException(Set<String> unknown) {
        super("UNKNOWN_PERMISSION", "Unknown permissions: " + unknown, "permissions");
    }
}
