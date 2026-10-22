package com.ecommerce.admin.stock.query;

import com.ecommerce.admin.stock.domain.Stock;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface StockQueryRepository extends Repository<Stock, Long> {

    String FROM_LEVELS = """
             FROM StockProduct p
             LEFT JOIN Stock s ON s.productId = p.id AND s.warehouse.id = :warehouseId
             WHERE p.archived = false
               AND (LOWER(p.name) LIKE :pattern ESCAPE '!' OR LOWER(p.sku) LIKE :pattern ESCAPE '!')
               AND (:maxQuantity IS NULL OR COALESCE(s.quantity, 0) <= :maxQuantity)
            """;

    @Query(value = """
            SELECT new com.ecommerce.admin.stock.query.StockLevelView(
                p.id, p.sku, p.name, p.categoryName, COALESCE(s.quantity, 0), s.updatedAt)
            """ + FROM_LEVELS,
            countQuery = "SELECT COUNT(p) " + FROM_LEVELS)
    Page<StockLevelView> findLevels(Long warehouseId, String pattern, Integer maxQuantity, Pageable pageable);

    String FROM_MOVEMENTS = """
             FROM StockMovement m
             JOIN StockProduct p ON p.id = m.productId
             JOIN m.warehouse w
             WHERE (:productId IS NULL OR p.id = :productId)
               AND (:warehouseId IS NULL OR w.id = :warehouseId)
            """;

    @Query(value = """
            SELECT new com.ecommerce.admin.stock.query.StockMovementView(
                m.id, p.id, p.sku, p.name, w.id, w.code, m.quantityChange, m.reason, m.note,
                m.createdByName, m.createdAt)
            """ + FROM_MOVEMENTS,
            countQuery = "SELECT COUNT(m) " + FROM_MOVEMENTS)
    Page<StockMovementView> findMovements(Long productId, Long warehouseId, Pageable pageable);

    @Query("SELECT COUNT(p) FROM StockProduct p WHERE p.archived = false")
    long countActiveProducts();

    @Query("""
            SELECT new com.ecommerce.admin.stock.query.WarehouseStockRow(
                w.id, w.code, w.name,
                COALESCE(SUM(CASE WHEN p.archived = false THEN s.quantity ELSE 0 END), 0),
                SUM(CASE WHEN p.archived = false AND s.quantity > 0 AND s.quantity <= :lowStockThreshold
                         THEN 1 ELSE 0 END),
                SUM(CASE WHEN p.archived = false AND s.quantity > 0 THEN 1 ELSE 0 END))
            FROM Warehouse w
            LEFT JOIN Stock s ON s.warehouse = w
            LEFT JOIN StockProduct p ON p.id = s.productId
            GROUP BY w.id, w.code, w.name
            ORDER BY w.code
            """)
    List<WarehouseStockRow> summarizeWarehouses(int lowStockThreshold);

    @Query("""
            SELECT new com.ecommerce.admin.stock.query.StockMovementView(
                m.id, p.id, p.sku, p.name, w.id, w.code, m.quantityChange, m.reason, m.note,
                m.createdByName, m.createdAt)
            FROM StockMovement m
            JOIN StockProduct p ON p.id = m.productId
            JOIN m.warehouse w
            WHERE m.id = :id
            """)
    Optional<StockMovementView> findMovementById(Long id);
}
