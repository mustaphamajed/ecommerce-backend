package com.ecommerce.admin.stock.exception;

import com.ecommerce.admin.common.exception.BadRequestException;

public class UnknownWarehouseException extends BadRequestException {

    public UnknownWarehouseException(Long warehouseId) {
        super("UNKNOWN_WAREHOUSE", "Warehouse " + warehouseId + " doesn't exist", "warehouseId");
    }
}
