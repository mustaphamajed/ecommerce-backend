package com.ecommerce.admin.dashboard.query;

import com.ecommerce.admin.product.domain.Product;
import com.ecommerce.admin.product.domain.ProductStatus;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface DashboardQueryRepository extends Repository<Product, Long> {

    @Query("SELECT COUNT(u) FROM User u WHERE u.enabled = :enabled")
    long countUsers(boolean enabled);

    @Query("""
            SELECT new com.ecommerce.admin.dashboard.query.WarehouseStockRow(
                w.id, w.code, w.name,
                COALESCE(SUM(CASE WHEN p.status = :active THEN s.quantity ELSE 0 END), 0),
                SUM(CASE WHEN p.status = :active AND s.quantity > 0 AND s.quantity <= :lowStockThreshold
                         THEN 1 ELSE 0 END),
                SUM(CASE WHEN p.status = :active AND s.quantity > 0 THEN 1 ELSE 0 END))
            FROM Warehouse w
            LEFT JOIN Stock s ON s.warehouse = w
            LEFT JOIN Product p ON p.id = s.productId
            GROUP BY w.id, w.code, w.name
            ORDER BY w.code
            """)
    List<WarehouseStockRow> summarizeWarehouses(ProductStatus active, int lowStockThreshold);
}
