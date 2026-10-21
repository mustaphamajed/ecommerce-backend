package com.ecommerce.admin.stock.query;

import java.time.Instant;

public record StockLevelView(
        Long productId,
        String sku,
        String productName,
        String categoryName,
        Integer quantity,
        Instant updatedAt) {
}
