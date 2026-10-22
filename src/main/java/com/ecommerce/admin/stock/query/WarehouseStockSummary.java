package com.ecommerce.admin.stock.query;

import org.springframework.modulith.NamedInterface;

@NamedInterface("queries")
public record WarehouseStockSummary(
        Long warehouseId,
        String code,
        String name,
        long totalUnits,
        long lowStockProducts,
        long outOfStockProducts) {
}
