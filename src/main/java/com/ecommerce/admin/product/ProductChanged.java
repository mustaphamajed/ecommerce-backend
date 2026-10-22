package com.ecommerce.admin.product;

public record ProductChanged(Long productId, String sku, String name, boolean archived) {
}
