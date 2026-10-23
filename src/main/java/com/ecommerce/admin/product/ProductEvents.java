package com.ecommerce.admin.product;

import com.ecommerce.admin.events.ProductChanged;
import com.ecommerce.admin.product.domain.Product;

public final class ProductEvents {

    private ProductEvents() {
    }

    public static ProductChanged changed(Product product) {
        String categoryName = product.getCategory() == null ? null : product.getCategory().getName();
        return new ProductChanged(product.getId(), product.getSku(), product.getName(), categoryName, product.isArchived());
    }
}
