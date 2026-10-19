package com.ecommerce.admin.category.query;

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
