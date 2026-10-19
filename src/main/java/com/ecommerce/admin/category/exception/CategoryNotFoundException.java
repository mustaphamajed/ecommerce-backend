package com.ecommerce.admin.category.exception;

import com.ecommerce.admin.common.exception.NotFoundException;

public class CategoryNotFoundException extends NotFoundException {

    public CategoryNotFoundException(Long id) {
        super("CATEGORY_NOT_FOUND", "Category " + id + " not found");
    }

    public CategoryNotFoundException(String slug) {
        super("CATEGORY_NOT_FOUND", "Category with slug " + slug + " not found");
    }
}
