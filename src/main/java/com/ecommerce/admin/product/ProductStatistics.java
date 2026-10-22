package com.ecommerce.admin.product;

import com.ecommerce.admin.product.category.domain.CategoryRepository;
import com.ecommerce.admin.product.domain.ProductRepository;
import com.ecommerce.admin.product.domain.ProductStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductStatistics {

    private final ProductRepository products;
    private final CategoryRepository categories;

    public ProductStatistics(ProductRepository products, CategoryRepository categories) {
        this.products = products;
        this.categories = categories;
    }

    public ProductCounts counts() {
        return new ProductCounts(
                products.countByStatus(ProductStatus.ACTIVE),
                products.countByStatus(ProductStatus.ARCHIVED),
                categories.count());
    }
}
