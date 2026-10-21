package com.ecommerce.admin.stock.domain;

import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface StockRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findByProductIdAndWarehouseId(Long productId, Long warehouseId);

    @Modifying(flushAutomatically = true)
    @Query("""
            UPDATE Stock s
            SET s.quantity = s.quantity + :change,
                s.version = s.version + 1,
                s.updatedAt = :now
            WHERE s.product.id = :productId
              AND s.warehouse.id = :warehouseId
              AND s.quantity + :change >= 0
            """)
    int applyChange(Long productId, Long warehouseId, int change, Instant now);
}
