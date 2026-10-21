package com.ecommerce.admin.product.exception;

import com.ecommerce.admin.common.exception.ConflictException;

public class DuplicateSkuException extends ConflictException {

    public DuplicateSkuException(String sku) {
        super("SKU_TAKEN", "A product with SKU " + sku + " already exists", "sku");
    }
}
