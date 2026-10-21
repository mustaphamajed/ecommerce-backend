package com.ecommerce.admin.stock.exception;

import com.ecommerce.admin.common.exception.ConflictException;

public class ProductArchivedException extends ConflictException {

    public ProductArchivedException(String sku) {
        super("PRODUCT_ARCHIVED", "Product " + sku + " is archived, restore it before changing its stock", "productId");
    }
}
