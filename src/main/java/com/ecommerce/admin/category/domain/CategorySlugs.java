package com.ecommerce.admin.category.domain;

import com.ecommerce.admin.category.exception.CategorySlugRequiredException;
import com.ecommerce.admin.common.Slugs;

public final class CategorySlugs {

    private CategorySlugs() {
    }

    public static String generateFrom(String name) {
        String slug = Slugs.from(name);
        if (slug.isEmpty()) {
            throw new CategorySlugRequiredException(name);
        }
        return slug;
    }
}
