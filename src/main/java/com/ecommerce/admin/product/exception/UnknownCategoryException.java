package com.ecommerce.admin.product.exception;

import com.ecommerce.admin.common.exception.BadRequestException;

public class UnknownCategoryException extends BadRequestException {

    public UnknownCategoryException(Long categoryId) {
        super("UNKNOWN_CATEGORY", "Category " + categoryId + " doesn't exist", "categoryId");
    }
}
