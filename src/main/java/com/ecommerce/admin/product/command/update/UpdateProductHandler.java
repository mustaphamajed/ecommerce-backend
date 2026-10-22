package com.ecommerce.admin.product.command.update;

import com.ecommerce.admin.common.CommandHandler;
import com.ecommerce.admin.product.ProductChanged;
import com.ecommerce.admin.product.domain.ProductCategories;
import com.ecommerce.admin.product.domain.ProductRepository;
import com.ecommerce.admin.product.exception.DuplicateSkuException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateProductHandler implements CommandHandler<UpdateProductCommand, Void> {

    private final ProductRepository repository;
    private final ProductCategories categories;
    private final ApplicationEventPublisher events;

    public UpdateProductHandler(
            ProductRepository repository, ProductCategories categories, ApplicationEventPublisher events) {
        this.repository = repository;
        this.categories = categories;
        this.events = events;
    }

    @Override
    public Void handle(UpdateProductCommand command) {
        var product = repository.findByIdOrThrow(command.id());
        if (repository.existsBySkuAndIdNot(command.sku(), command.id())) {
            throw new DuplicateSkuException(command.sku());
        }
        product.update(
                command.sku(),
                command.name(),
                command.description(),
                command.price(),
                categories.resolve(command.categoryId()));
        events.publishEvent(new ProductChanged(product.getId(), product.getSku(), product.getName(), product.isArchived()));
        return null;
    }
}
