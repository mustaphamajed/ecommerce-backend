package com.ecommerce.admin.dashboard.query;

public record WarehouseStockSummary(
        Long warehouseId,
        String code,
        String name,
        long totalUnits,
        long lowStockProducts,
        long outOfStockProducts) {
}
