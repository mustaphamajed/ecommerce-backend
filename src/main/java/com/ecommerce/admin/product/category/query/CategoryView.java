package com.ecommerce.admin.product.category.query;

import java.time.Instant;

public record CategoryView(
        Long id,
        String name,
        String slug,
        String description,
        Long productCount,
        Instant createdAt,
        Instant updatedAt) {
}
