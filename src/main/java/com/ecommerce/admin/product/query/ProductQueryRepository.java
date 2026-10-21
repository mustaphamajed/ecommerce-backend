package com.ecommerce.admin.product.query;

import com.ecommerce.admin.common.media.MediaService;
import com.ecommerce.admin.product.domain.Product;
import com.ecommerce.admin.product.domain.ProductStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface ProductQueryRepository extends Repository<Product, Long> {

    String IMAGE_URL = "CASE WHEN p.imageKey IS NULL THEN NULL ELSE CONCAT('"
            + MediaService.URL_PREFIX + "', p.imageKey) END";

    String SELECT_VIEW = """
            SELECT new com.ecommerce.admin.product.query.ProductView(
                p.id, p.sku, p.name, p.description, p.price, p.status,
                c.id, c.name,
            """ + IMAGE_URL + ", p.createdAt, p.updatedAt)";

    String FROM_FILTERED = """
             FROM Product p LEFT JOIN p.category c
             WHERE (LOWER(p.name) LIKE :pattern ESCAPE '!' OR LOWER(p.sku) LIKE :pattern ESCAPE '!')
               AND (:categoryId IS NULL OR c.id = :categoryId)
               AND (:status IS NULL OR p.status = :status)
            """;

    @Query(value = SELECT_VIEW + FROM_FILTERED, countQuery = "SELECT COUNT(p) " + FROM_FILTERED)
    Page<ProductView> search(String pattern, Long categoryId, ProductStatus status, Pageable pageable);

    @Query(SELECT_VIEW + " FROM Product p LEFT JOIN p.category c WHERE p.id = :id")
    Optional<ProductView> findViewById(Long id);
}
