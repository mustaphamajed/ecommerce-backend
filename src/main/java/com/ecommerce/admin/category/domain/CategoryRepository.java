package com.ecommerce.admin.category.domain;

import com.ecommerce.admin.category.exception.CategoryNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    default Category findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
    }
}
