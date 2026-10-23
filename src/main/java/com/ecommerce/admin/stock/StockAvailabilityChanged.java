package com.ecommerce.admin.stock;

public record StockAvailabilityChanged(Long productId, boolean inStock) {
}
