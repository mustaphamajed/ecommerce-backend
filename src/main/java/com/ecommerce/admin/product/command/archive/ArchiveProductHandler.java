package com.ecommerce.admin.product.command.archive;

import com.ecommerce.admin.common.CommandHandler;
import com.ecommerce.admin.product.ProductMetrics;
import com.ecommerce.admin.product.domain.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ArchiveProductHandler implements CommandHandler<ArchiveProductCommand, Void> {

    private final ProductRepository repository;
    private final ProductMetrics metrics;

    public ArchiveProductHandler(ProductRepository repository, ProductMetrics metrics) {
        this.repository = repository;
        this.metrics = metrics;
    }

    @Override
    public Void handle(ArchiveProductCommand command) {
        repository.findByIdOrThrow(command.productId()).archive();
        metrics.productArchived();
        return null;
    }
}
