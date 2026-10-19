package com.ecommerce.admin.user.exception;

import com.ecommerce.admin.common.exception.BadRequestException;
import java.util.Set;

public class UnknownRolesException extends BadRequestException {

    public UnknownRolesException(Set<Long> unknownIds) {
        super("UNKNOWN_ROLES", "Unknown role ids: " + unknownIds, "roleIds");
    }
}
