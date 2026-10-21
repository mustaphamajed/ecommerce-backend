package com.ecommerce.admin.product.command.update;

import com.ecommerce.admin.common.CommandHandler;
import com.ecommerce.admin.product.domain.ProductCategories;
import com.ecommerce.admin.product.domain.ProductRepository;
import com.ecommerce.admin.product.exception.DuplicateSkuException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateProductHandler implements CommandHandler<UpdateProductCommand, Void> {

    private final ProductRepository repository;
    private final ProductCategories categories;

    public UpdateProductHandler(ProductRepository repository, ProductCategories categories) {
        this.repository = repository;
        this.categories = categories;
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
        return null;
    }
}
