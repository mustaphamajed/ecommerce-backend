package com.ecommerce.admin.stock.api;

import com.ecommerce.admin.stock.domain.MovementReason;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AdjustStockRequest(
        @NotNull Long productId,
        @NotNull Long warehouseId,
        @NotNull @Min(-1_000_000) @Max(1_000_000) Integer quantityChange,
        @NotNull MovementReason reason,
        @Size(max = 500) String note) {
}
