package com.ecommerce.admin.category.exception;

import com.ecommerce.admin.common.exception.ConflictException;

public class DuplicateCategoryNameException extends ConflictException {

    public DuplicateCategoryNameException(String name) {
        super("CATEGORY_NAME_TAKEN", "A category named " + name + " already exists", "name");
    }
}
