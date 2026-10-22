package com.ecommerce.admin.stock.query;

public record WarehouseStockRow(
        Long warehouseId,
        String code,
        String name,
        Long totalUnits,
        Long lowStockProducts,
        Long inStockProducts) {
}
