package com.ecommerce.admin.product.domain;

import com.ecommerce.admin.category.domain.Category;
import com.ecommerce.admin.category.domain.CategoryRepository;
import com.ecommerce.admin.product.exception.UnknownCategoryException;
import org.springframework.stereotype.Component;

@Component
public class ProductCategories {

    private final CategoryRepository categoryRepository;

    public ProductCategories(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category resolve(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new UnknownCategoryException(categoryId));
    }
}
