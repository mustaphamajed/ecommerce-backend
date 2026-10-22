package com.ecommerce.admin.product.category.command.update;

import com.ecommerce.admin.common.Strings;

public record UpdateCategoryCommand(Long id, String name, String description, String slug) {

    public UpdateCategoryCommand {
        name = name.trim();
        description = Strings.trimToNull(description);
        slug = Strings.trimToNull(slug);
    }
}
