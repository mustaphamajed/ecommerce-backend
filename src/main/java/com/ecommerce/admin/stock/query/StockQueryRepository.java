package com.ecommerce.admin.stock.query;

import com.ecommerce.admin.stock.domain.Stock;
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
             LEFT JOIN m.createdBy u
             WHERE (:productId IS NULL OR p.id = :productId)
               AND (:warehouseId IS NULL OR w.id = :warehouseId)
            """;

    @Query(value = """
            SELECT new com.ecommerce.admin.stock.query.StockMovementView(
                m.id, p.id, p.sku, p.name, w.id, w.code, m.quantityChange, m.reason, m.note,
                CONCAT(u.firstName, ' ', u.lastName), m.createdAt)
            """ + FROM_MOVEMENTS,
            countQuery = "SELECT COUNT(m) " + FROM_MOVEMENTS)
    Page<StockMovementView> findMovements(Long productId, Long warehouseId, Pageable pageable);

    @Query("""
            SELECT new com.ecommerce.admin.stock.query.StockMovementView(
                m.id, p.id, p.sku, p.name, w.id, w.code, m.quantityChange, m.reason, m.note,
                CONCAT(u.firstName, ' ', u.lastName), m.createdAt)
            FROM StockMovement m
            JOIN StockProduct p ON p.id = m.productId
            JOIN m.warehouse w
            LEFT JOIN m.createdBy u
            WHERE m.id = :id
            """)
    Optional<StockMovementView> findMovementById(Long id);
}
