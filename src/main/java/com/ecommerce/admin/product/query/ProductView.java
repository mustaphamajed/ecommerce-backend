package com.ecommerce.admin.product.query;

import com.ecommerce.admin.product.domain.ProductStatus;
import java.math.BigDecimal;
import java.time.Instant;

public record ProductView(
        Long id,
        String sku,
        String name,
        String description,
        BigDecimal price,
        ProductStatus status,
        Long categoryId,
        String categoryName,
        String imageUrl,
        Instant createdAt,
        Instant updatedAt) {
}
