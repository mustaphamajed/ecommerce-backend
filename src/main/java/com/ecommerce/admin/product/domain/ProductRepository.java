package com.ecommerce.admin.product.domain;

import com.ecommerce.admin.product.exception.ProductNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {

    long countByStatus(ProductStatus status);

    @Modifying
    @Query("UPDATE Product p SET p.inStock = :inStock WHERE p.id = :id")
    int updateAvailability(Long id, boolean inStock);

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, Long id);

    default Product findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }
}
