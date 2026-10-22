package com.ecommerce.admin.product.category.command.create;

import com.ecommerce.admin.common.Strings;

public record CreateCategoryCommand(String name, String description, String slug) {

    public CreateCategoryCommand {
        name = name.trim();
        description = Strings.trimToNull(description);
        slug = Strings.trimToNull(slug);
    }
}
