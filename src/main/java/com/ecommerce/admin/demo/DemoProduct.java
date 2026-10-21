package com.ecommerce.admin.demo;

import java.math.BigDecimal;

public record DemoProduct(
        String sku, String name, String description, BigDecimal price, String category, boolean archived) {

    static DemoProduct active(String sku, String name, String description, String price, String category) {
        return new DemoProduct(sku, name, description, new BigDecimal(price), category, false);
    }

    static DemoProduct archived(String sku, String name, String description, String price, String category) {
        return new DemoProduct(sku, name, description, new BigDecimal(price), category, true);
    }
}
