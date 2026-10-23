package com.ecommerce.admin.events;

public record ProductChanged(Long productId, String sku, String name, String categoryName, boolean archived) {
}
