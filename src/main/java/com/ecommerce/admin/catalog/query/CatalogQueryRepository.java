package com.ecommerce.admin.catalog.query;

import com.ecommerce.admin.product.domain.Product;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface CatalogQueryRepository extends Repository<Product, Long> {

    String FROM_ACTIVE_PRODUCTS = """
             FROM Product p LEFT JOIN p.category c
             WHERE p.status = com.ecommerce.admin.product.domain.ProductStatus.ACTIVE
               AND (LOWER(p.name) LIKE :pattern ESCAPE '!' OR LOWER(p.sku) LIKE :pattern ESCAPE '!')
               AND (:categorySlug IS NULL OR c.slug = :categorySlug)
            """;

    @Query(value = """
            SELECT new com.ecommerce.admin.catalog.query.CatalogProductView(
                p.id, p.sku, p.name, p.description, p.price, c.name, c.slug)
            """ + FROM_ACTIVE_PRODUCTS,
            countQuery = "SELECT COUNT(p) " + FROM_ACTIVE_PRODUCTS)
    Page<CatalogProductView> searchProducts(String pattern, String categorySlug, Pageable pageable);

    @Query("""
            SELECT new com.ecommerce.admin.catalog.query.CatalogCategoryView(c.id, c.name, c.slug)
            FROM Category c
            WHERE EXISTS (
                SELECT 1 FROM Product p
                WHERE p.category = c AND p.status = com.ecommerce.admin.product.domain.ProductStatus.ACTIVE)
            ORDER BY c.name
            """)
    List<CatalogCategoryView> findCategoriesWithActiveProducts();
}
