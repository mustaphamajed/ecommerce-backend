package com.ecommerce.admin.category.exception;

import com.ecommerce.admin.common.exception.BadRequestException;

public class CategorySlugRequiredException extends BadRequestException {

    public CategorySlugRequiredException(String name) {
        super("CATEGORY_SLUG_REQUIRED", "Can't generate a slug from the name '" + name + "', provide one", "slug");
    }
}
