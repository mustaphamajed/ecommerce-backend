package com.ecommerce.admin.stock.domain;

import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface StockRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findByProductIdAndWarehouseId(Long productId, Long warehouseId);

    @Modifying
    @Query(nativeQuery = true, value = """
            INSERT INTO stock (product_id, warehouse_id, quantity, version, updated_at)
            VALUES (:productId, :warehouseId, 0, 0, :now)
            ON CONFLICT (product_id, warehouse_id) DO NOTHING
            """)
    void createIfMissing(Long productId, Long warehouseId, Instant now);

    @Modifying(flushAutomatically = true)
    @Query("""
            UPDATE Stock s
            SET s.quantity = s.quantity + :change,
                s.version = s.version + 1,
                s.updatedAt = :now
            WHERE s.productId = :productId
              AND s.warehouse.id = :warehouseId
              AND s.quantity + :change >= 0
            """)
    int applyChange(Long productId, Long warehouseId, int change, Instant now);
}
