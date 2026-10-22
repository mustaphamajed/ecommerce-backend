package com.ecommerce.admin.product.command.create;

import com.ecommerce.admin.common.CommandHandler;
import com.ecommerce.admin.product.ProductMetrics;
import com.ecommerce.admin.product.domain.Product;
import com.ecommerce.admin.product.domain.ProductCategories;
import com.ecommerce.admin.product.domain.ProductRepository;
import com.ecommerce.admin.product.exception.DuplicateSkuException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateProductHandler implements CommandHandler<CreateProductCommand, Long> {

    private final ProductRepository repository;
    private final ProductCategories categories;
    private final ProductMetrics metrics;

    public CreateProductHandler(ProductRepository repository, ProductCategories categories, ProductMetrics metrics) {
        this.repository = repository;
        this.categories = categories;
        this.metrics = metrics;
    }

    @Override
    public Long handle(CreateProductCommand command) {
        if (repository.existsBySku(command.sku())) {
            throw new DuplicateSkuException(command.sku());
        }
        var product = new Product(
                command.sku(),
                command.name(),
                command.description(),
                command.price(),
                categories.resolve(command.categoryId()));
        Long id = repository.save(product).getId();
        metrics.productCreated();
        return id;
    }
}
