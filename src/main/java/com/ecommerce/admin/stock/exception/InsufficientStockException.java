package com.ecommerce.admin.stock.exception;

import com.ecommerce.admin.common.exception.ConflictException;

public class InsufficientStockException extends ConflictException {

    public InsufficientStockException(int available, int requested) {
        super("INSUFFICIENT_STOCK",
                "Not enough stock: " + available + " available, " + requested + " requested",
                "quantityChange");
    }
}
