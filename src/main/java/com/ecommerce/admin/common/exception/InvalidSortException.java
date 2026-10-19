package com.ecommerce.admin.common.exception;

import java.util.Set;

public class InvalidSortException extends BadRequestException {

    public InvalidSortException(String property, Set<String> allowed) {
        super("INVALID_SORT", "Can't sort by " + property + ", allowed: " + allowed, "sort");
    }
}
