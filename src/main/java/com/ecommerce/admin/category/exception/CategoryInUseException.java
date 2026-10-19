package com.ecommerce.admin.category.exception;

import com.ecommerce.admin.common.exception.ConflictException;

public class CategoryInUseException extends ConflictException {

    public CategoryInUseException(Long id) {
        super("CATEGORY_IN_USE", "Category " + id + " still has products and can't be deleted");
    }
}
