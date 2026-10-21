package com.ecommerce.admin.stock.exception;

import com.ecommerce.admin.common.exception.BadRequestException;

public class UnknownStockProductException extends BadRequestException {

    public UnknownStockProductException(Long productId) {
        super("UNKNOWN_PRODUCT", "Product " + productId + " doesn't exist", "productId");
    }
}
