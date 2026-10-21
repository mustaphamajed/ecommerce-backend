package com.ecommerce.admin.product.exception;

import com.ecommerce.admin.common.exception.NotFoundException;

public class ProductNotFoundException extends NotFoundException {

    public ProductNotFoundException(Long id) {
        super("PRODUCT_NOT_FOUND", "Product " + id + " not found");
    }
}
