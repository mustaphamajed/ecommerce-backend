package com.ecommerce.admin.product.command.archive;

import com.ecommerce.admin.common.CommandHandler;
import com.ecommerce.admin.product.ProductEvents;
import com.ecommerce.admin.product.ProductMetrics;
import com.ecommerce.admin.product.domain.ProductRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ArchiveProductHandler implements CommandHandler<ArchiveProductCommand, Void> {

    private final ProductRepository repository;
    private final ProductMetrics metrics;
    private final ApplicationEventPublisher events;

    public ArchiveProductHandler(
            ProductRepository repository, ProductMetrics metrics, ApplicationEventPublisher events) {
        this.repository = repository;
        this.metrics = metrics;
        this.events = events;
    }

    @Override
    public Void handle(ArchiveProductCommand command) {
        var product = repository.findByIdOrThrow(command.productId());
        product.archive();
        metrics.productArchived();
        events.publishEvent(ProductEvents.changed(product));
        return null;
    }
}
