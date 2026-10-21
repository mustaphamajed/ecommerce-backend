package com.ecommerce.admin.product.exception;

import com.ecommerce.admin.common.exception.NotFoundException;

public class ProductImageNotFoundException extends NotFoundException {

    public ProductImageNotFoundException(Long productId) {
        super("PRODUCT_IMAGE_NOT_FOUND", "Product " + productId + " has no image");
    }
}
