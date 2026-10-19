package com.ecommerce.admin.user.exception;

import com.ecommerce.admin.common.exception.BadRequestException;
import java.util.Set;

public class UnknownWarehousesException extends BadRequestException {

    public UnknownWarehousesException(Set<Long> unknownIds) {
        super("UNKNOWN_WAREHOUSES", "Unknown warehouse ids: " + unknownIds, "warehouseIds");
    }
}
