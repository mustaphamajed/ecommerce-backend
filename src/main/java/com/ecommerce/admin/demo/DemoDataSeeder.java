package com.ecommerce.admin.demo;

import com.ecommerce.admin.category.command.create.CreateCategoryCommand;
import com.ecommerce.admin.category.command.create.CreateCategoryHandler;
import com.ecommerce.admin.category.domain.Category;
import com.ecommerce.admin.category.domain.CategoryRepository;
import com.ecommerce.admin.product.command.archive.ArchiveProductCommand;
import com.ecommerce.admin.product.command.archive.ArchiveProductHandler;
import com.ecommerce.admin.product.command.create.CreateProductCommand;
import com.ecommerce.admin.product.command.create.CreateProductHandler;
import com.ecommerce.admin.product.domain.ProductRepository;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(name = "app.seed.demo-data", havingValue = "true")
public class DemoDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CreateCategoryHandler createCategory;
    private final CreateProductHandler createProduct;
    private final ArchiveProductHandler archiveProduct;

    public DemoDataSeeder(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            CreateCategoryHandler createCategory,
            CreateProductHandler createProduct,
            ArchiveProductHandler archiveProduct) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.createCategory = createCategory;
        this.createProduct = createProduct;
        this.archiveProduct = archiveProduct;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (productRepository.count() > 0) {
            log.info("Demo data skipped: products already exist");
            return;
        }

        Map<String, Long> categoryIds = new HashMap<>();
        for (DemoCategory category : DemoCatalog.CATEGORIES) {
            categoryIds.put(category.name(), findOrCreateCategory(category));
        }

        for (DemoProduct product : DemoCatalog.PRODUCTS) {
            Long productId = createProduct.handle(new CreateProductCommand(
                    product.sku(),
                    product.name(),
                    product.description(),
                    product.price(),
                    categoryIds.get(product.category())));
            if (product.archived()) {
                archiveProduct.handle(new ArchiveProductCommand(productId));
            }
        }

        log.info("Seeded {} demo products in {} categories",
                DemoCatalog.PRODUCTS.size(), DemoCatalog.CATEGORIES.size());
    }

    private Long findOrCreateCategory(DemoCategory category) {
        return categoryRepository.findByNameIgnoreCase(category.name())
                .map(Category::getId)
                .orElseGet(() -> createCategory.handle(
                        new CreateCategoryCommand(category.name(), category.description(), null)));
    }
}
