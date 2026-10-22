package com.ecommerce.admin.stock.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

class MovementReasonTest {

    @ParameterizedTest(name = "{0} with {1} → {2}")
    @CsvSource({
        "PURCHASE,      5,  true",
        "PURCHASE,     -5,  false",
        "RETURN,        2,  true",
        "RETURN,       -2,  false",
        "SALE,         -1,  true",
        "SALE,          1,  false",
        "ADJUSTMENT,    3,  true",
        "ADJUSTMENT,   -3,  true",
        "ADJUSTMENT,    0,  false",
        "TRANSFER_IN,   4,  true",
        "TRANSFER_OUT, -4,  true",
        "TRANSFER_OUT,  4,  false",
    })
    void acceptsOnlyChangesInTheRightDirection(MovementReason reason, int quantityChange, boolean expected) {
        assertThat(reason.accepts(quantityChange)).isEqualTo(expected);
    }

    @ParameterizedTest
    @EnumSource(value = MovementReason.class, names = {"PURCHASE", "SALE", "ADJUSTMENT", "RETURN"})
    void manualReasonsCanBeEnteredByUsers(MovementReason reason) {
        assertThat(reason.isManual()).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = MovementReason.class, names = {"TRANSFER_IN", "TRANSFER_OUT"})
    void transfersAreReservedForTheSystem(MovementReason reason) {
        assertThat(reason.isManual()).isFalse();
    }
}
