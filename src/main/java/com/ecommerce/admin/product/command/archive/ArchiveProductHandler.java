package com.ecommerce.admin.product.command.archive;

import com.ecommerce.admin.common.CommandHandler;
import com.ecommerce.admin.product.domain.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ArchiveProductHandler implements CommandHandler<ArchiveProductCommand, Void> {

    private final ProductRepository repository;

    public ArchiveProductHandler(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public Void handle(ArchiveProductCommand command) {
        repository.findByIdOrThrow(command.productId()).archive();
        return null;
    }
}
