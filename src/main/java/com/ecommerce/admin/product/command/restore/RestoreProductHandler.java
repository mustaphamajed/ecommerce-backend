package com.ecommerce.admin.product.command.restore;

import com.ecommerce.admin.common.CommandHandler;
import com.ecommerce.admin.product.ProductChanged;
import com.ecommerce.admin.product.domain.ProductRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RestoreProductHandler implements CommandHandler<RestoreProductCommand, Void> {

    private final ProductRepository repository;
    private final ApplicationEventPublisher events;

    public RestoreProductHandler(ProductRepository repository, ApplicationEventPublisher events) {
        this.repository = repository;
        this.events = events;
    }

    @Override
    public Void handle(RestoreProductCommand command) {
        var product = repository.findByIdOrThrow(command.productId());
        product.restore();
        events.publishEvent(new ProductChanged(product.getId(), product.getSku(), product.getName(), product.isArchived()));
        return null;
    }
}
