package com.ecommerce.admin.stock.domain;

public enum MovementReason {
    PURCHASE,
    SALE,
    ADJUSTMENT,
    RETURN,
    TRANSFER_IN,
    TRANSFER_OUT;

    public boolean isManual() {
        return this != TRANSFER_IN && this != TRANSFER_OUT;
    }

    public boolean accepts(int quantityChange) {
        return switch (this) {
            case PURCHASE, RETURN, TRANSFER_IN -> quantityChange > 0;
            case SALE, TRANSFER_OUT -> quantityChange < 0;
            case ADJUSTMENT -> quantityChange != 0;
        };
    }
}
