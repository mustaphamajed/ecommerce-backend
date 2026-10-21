package com.ecommerce.admin.stock.exception;

import com.ecommerce.admin.common.exception.ForbiddenException;

public class WarehouseAccessDeniedException extends ForbiddenException {

    public WarehouseAccessDeniedException(String warehouseCode) {
        super("WAREHOUSE_FORBIDDEN", "You can't change stock in warehouse " + warehouseCode);
    }
}
