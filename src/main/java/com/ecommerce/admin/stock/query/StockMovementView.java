package com.ecommerce.admin.stock.query;

import com.ecommerce.admin.stock.domain.MovementReason;
import java.time.Instant;
import org.springframework.modulith.NamedInterface;

@NamedInterface("queries")
public record StockMovementView(
        Long id,
        Long productId,
        String sku,
        String productName,
        Long warehouseId,
        String warehouseCode,
        int quantityChange,
        MovementReason reason,
        String note,
        String createdBy,
        Instant createdAt) {
}
