package com.ecommerce.admin.catalog.query;

import java.math.BigDecimal;

public record CatalogProductView(
        Long id,
        String sku,
        String name,
        String description,
        BigDecimal price,
        String categoryName,
        String categorySlug,
        String imageUrl) {
}
