package com.ecommerce.admin.stock;

import com.ecommerce.admin.common.transaction.TransactionCallbacks;
import com.ecommerce.admin.stock.domain.MovementReason;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class StockMetrics {

    private final MeterRegistry registry;

    public StockMetrics(MeterRegistry registry) {
        this.registry = registry;
    }

    public void movementRecorded(MovementReason reason, int quantityChange) {
        String reasonTag = reason.name().toLowerCase();
        TransactionCallbacks.afterCommit(() -> {
            Counter.builder("stock.movements")
                    .description("Stock movements recorded")
                    .tag("reason", reasonTag)
                    .register(registry)
                    .increment();
            Counter.builder("stock.units.moved")
                    .description("Units moved in or out of stock")
                    .tag("reason", reasonTag)
                    .register(registry)
                    .increment(Math.abs(quantityChange));
        });
    }
}
