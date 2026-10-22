package com.ecommerce.admin.dashboard.query;

import com.ecommerce.admin.product.ProductStatistics;
import com.ecommerce.admin.role.domain.Permission;
import com.ecommerce.admin.stock.query.StockQueryService;
import com.ecommerce.admin.stock.query.WarehouseStockSummary;
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
    private final ProductStatistics productStatistics;

    public DashboardQueryService(
            DashboardQueryRepository repository, StockQueryService stockQueries, ProductStatistics productStatistics) {
        this.repository = repository;
        this.stockQueries = stockQueries;
        this.productStatistics = productStatistics;
    }

    public DashboardView findDashboard(Collection<String> authorities) {
        return new DashboardView(
                authorities.contains(Permission.PRODUCT_READ.authority()) ? catalogStats() : null,
                authorities.contains(Permission.STOCK_READ.authority()) ? stockStats() : null,
                authorities.contains(Permission.USER_READ.authority()) ? userStats() : null);
    }

    private CatalogStats catalogStats() {
        var counts = productStatistics.counts();
        return new CatalogStats(counts.active(), counts.archived(), counts.categories());
    }

    private StockStats stockStats() {
        var warehouses = stockQueries.summarizeWarehouses();
        long totalUnits = warehouses.stream().mapToLong(WarehouseStockSummary::totalUnits).sum();
        var recentMovements = stockQueries.findMovements(null, null, PageRequest.of(0, RECENT_MOVEMENTS)).content();
        return new StockStats(totalUnits, StockQueryService.LOW_STOCK_THRESHOLD, warehouses, recentMovements);
    }

    private UserStats userStats() {
        return new UserStats(repository.countUsers(true), repository.countUsers(false));
    }
}
