package com.ecommerce.admin.product;

import com.ecommerce.admin.product.domain.Product;

public record ProductChanged(Long productId, String sku, String name, String categoryName, boolean archived) {

    public static ProductChanged from(Product product) {
        String categoryName = product.getCategory() == null ? null : product.getCategory().getName();
        return new ProductChanged(product.getId(), product.getSku(), product.getName(), categoryName, product.isArchived());
    }
}
