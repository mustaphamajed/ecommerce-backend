package com.ecommerce.admin.product.command.restore;

import com.ecommerce.admin.common.CommandHandler;
import com.ecommerce.admin.product.domain.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RestoreProductHandler implements CommandHandler<RestoreProductCommand, Void> {

    private final ProductRepository repository;

    public RestoreProductHandler(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public Void handle(RestoreProductCommand command) {
        repository.findByIdOrThrow(command.productId()).restore();
        return null;
    }
}
