package com.ecommerce.admin.product;

import com.ecommerce.admin.common.transaction.TransactionCallbacks;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class ProductMetrics {

    private final Counter created;
    private final Counter archived;

    public ProductMetrics(MeterRegistry registry) {
        this.created = Counter.builder("products.added")
                .description("Products created from the admin panel")
                .register(registry);
        this.archived = Counter.builder("products.archived")
                .description("Products archived from the admin panel")
                .register(registry);
    }

    public void productCreated() {
        TransactionCallbacks.afterCommit(created::increment);
    }

    public void productArchived() {
        TransactionCallbacks.afterCommit(archived::increment);
    }
}
