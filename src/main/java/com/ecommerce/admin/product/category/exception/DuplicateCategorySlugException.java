package com.ecommerce.admin.product.category.exception;

import com.ecommerce.admin.common.exception.ConflictException;

public class DuplicateCategorySlugException extends ConflictException {

    public DuplicateCategorySlugException(String slug) {
        super("CATEGORY_SLUG_TAKEN", "A category with slug " + slug + " already exists", "slug");
    }
}
