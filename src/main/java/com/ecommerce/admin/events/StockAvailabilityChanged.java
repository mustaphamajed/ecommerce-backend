package com.ecommerce.admin.events;

public record StockAvailabilityChanged(Long productId, boolean inStock) {
}
