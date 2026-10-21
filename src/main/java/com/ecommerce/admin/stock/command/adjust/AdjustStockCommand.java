package com.ecommerce.admin.stock.command.adjust;

import com.ecommerce.admin.common.Strings;
import com.ecommerce.admin.stock.domain.MovementReason;

public record AdjustStockCommand(
        Long productId,
        Long warehouseId,
        int quantityChange,
        MovementReason reason,
        String note,
        Long actorId) {

    public AdjustStockCommand {
        note = Strings.trimToNull(note);
    }
}
