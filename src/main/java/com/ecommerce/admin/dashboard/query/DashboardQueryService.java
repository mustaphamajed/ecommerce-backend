package com.ecommerce.admin.dashboard.query;

import com.ecommerce.admin.product.domain.ProductStatus;
import com.ecommerce.admin.role.domain.Permission;
import com.ecommerce.admin.stock.domain.Stock;
import com.ecommerce.admin.stock.query.StockQueryService;
import java.util.Collection;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DashboardQueryService {

    private static final int RECENT_MOVEMENTS = 5;

    private final DashboardQueryRepository repository;
    private final StockQueryService stockQueries;

    public DashboardQueryService(DashboardQueryRepository repository, StockQueryService stockQueries) {
        this.repository = repository;
        this.stockQueries = stockQueries;
    }

    public DashboardView findDashboard(Collection<String> authorities) {
        return new DashboardView(
                authorities.contains(Permission.PRODUCT_READ.authority()) ? catalogStats() : null,
                authorities.contains(Permission.STOCK_READ.authority()) ? stockStats() : null,
                authorities.contains(Permission.USER_READ.authority()) ? userStats() : null);
    }

    private CatalogStats catalogStats() {
        return new CatalogStats(
                repository.countProducts(ProductStatus.ACTIVE),
                repository.countProducts(ProductStatus.ARCHIVED),
                repository.countCategories());
    }

    private StockStats stockStats() {
        long activeProducts = repository.countProducts(ProductStatus.ACTIVE);
        var warehouses = repository.summarizeWarehouses(ProductStatus.ACTIVE, Stock.LOW_STOCK_THRESHOLD).stream()
                .map(row -> new WarehouseStockSummary(
                        row.warehouseId(),
                        row.code(),
                        row.name(),
                        row.totalUnits(),
                        row.lowStockProducts(),
                        activeProducts - row.inStockProducts()))
                .toList();
        long totalUnits = warehouses.stream().mapToLong(WarehouseStockSummary::totalUnits).sum();
        var recentMovements = stockQueries.findMovements(null, null, PageRequest.of(0, RECENT_MOVEMENTS)).content();
        return new StockStats(totalUnits, Stock.LOW_STOCK_THRESHOLD, warehouses, recentMovements);
    }

    private UserStats userStats() {
        return new UserStats(repository.countUsers(true), repository.countUsers(false));
    }
}
