package com.ecommerce.admin.dashboard.query;

import com.ecommerce.admin.stock.query.StockMovementView;
import com.ecommerce.admin.stock.query.WarehouseStockSummary;
import java.util.List;

public record StockStats(
        long totalUnits,
        int lowStockThreshold,
        List<WarehouseStockSummary> warehouses,
        List<StockMovementView> recentMovements) {
}
