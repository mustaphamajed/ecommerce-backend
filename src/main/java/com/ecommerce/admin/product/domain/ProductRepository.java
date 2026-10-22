package com.ecommerce.admin.product.domain;

import com.ecommerce.admin.product.exception.ProductNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    long countByStatus(ProductStatus status);

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, Long id);

    default Product findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }
}
