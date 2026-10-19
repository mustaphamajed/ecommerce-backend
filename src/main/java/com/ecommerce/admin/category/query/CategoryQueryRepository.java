package com.ecommerce.admin.category.query;

import com.ecommerce.admin.category.domain.Category;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface CategoryQueryRepository extends Repository<Category, Long> {

    String SELECT_VIEW = """
            SELECT new com.ecommerce.admin.category.query.CategoryView(
                c.id, c.name, c.slug, c.description, COUNT(p), c.createdAt, c.updatedAt)
            FROM Category c
            LEFT JOIN Product p ON p.category = c
            """;

    String GROUP_BY = " GROUP BY c.id, c.name, c.slug, c.description, c.createdAt, c.updatedAt";

    String WHERE_NAME_MATCHES = " WHERE LOWER(c.name) LIKE :pattern ESCAPE '!'";

    @Query(value = SELECT_VIEW + WHERE_NAME_MATCHES + GROUP_BY,
            countQuery = "SELECT COUNT(c) FROM Category c" + WHERE_NAME_MATCHES)
    Page<CategoryView> search(String pattern, Pageable pageable);

    @Query(SELECT_VIEW + " WHERE c.id = :id" + GROUP_BY)
    Optional<CategoryView> findViewById(Long id);

    @Query(SELECT_VIEW + " WHERE c.slug = :slug" + GROUP_BY)
    Optional<CategoryView> findViewBySlug(String slug);
}
