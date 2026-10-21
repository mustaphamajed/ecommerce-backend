package com.ecommerce.admin.stock.exception;

import com.ecommerce.admin.common.exception.BadRequestException;
import com.ecommerce.admin.stock.domain.MovementReason;

public class InvalidMovementException extends BadRequestException {

    private InvalidMovementException(String message, String property) {
        super("INVALID_MOVEMENT", message, property);
    }

    public static InvalidMovementException notManual(MovementReason reason) {
        return new InvalidMovementException(reason + " movements can only be created by a warehouse transfer", "reason");
    }

    public static InvalidMovementException wrongDirection(MovementReason reason, int quantityChange) {
        return new InvalidMovementException(
                "A " + reason + " movement can't change the stock by " + quantityChange, "quantityChange");
    }
}
