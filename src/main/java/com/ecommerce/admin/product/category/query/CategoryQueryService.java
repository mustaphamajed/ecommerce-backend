package com.ecommerce.admin.product.category.query;

import com.ecommerce.admin.product.category.exception.CategoryNotFoundException;
import com.ecommerce.admin.common.AllowedSort;
import com.ecommerce.admin.common.PageResponse;
import com.ecommerce.admin.common.SearchPatterns;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CategoryQueryService {

    private static final Set<String> SORTABLE_PROPERTIES = Set.of("name", "slug", "createdAt", "updatedAt");

    private final CategoryQueryRepository repository;

    public CategoryQueryService(CategoryQueryRepository repository) {
        this.repository = repository;
    }

    public PageResponse<CategoryView> search(String query, Pageable pageable) {
        AllowedSort.check(pageable, SORTABLE_PROPERTIES);
        return PageResponse.from(repository.search(SearchPatterns.contains(query), pageable));
    }

    public CategoryView findById(Long id) {
        return repository.findViewById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    public CategoryView findBySlug(String slug) {
        return repository.findViewBySlug(slug)
                .orElseThrow(() -> new CategoryNotFoundException(slug));
    }
}
