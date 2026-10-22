package com.ecommerce.admin.product.category.domain;

import com.ecommerce.admin.product.category.exception.CategoryNotFoundException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    default Category findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
    }
}
