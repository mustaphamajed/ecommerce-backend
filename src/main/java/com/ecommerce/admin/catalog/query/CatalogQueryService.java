package com.ecommerce.admin.catalog.query;

import com.ecommerce.admin.common.AllowedSort;
import com.ecommerce.admin.common.PageResponse;
import com.ecommerce.admin.common.SearchPatterns;
import com.ecommerce.admin.common.Strings;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CatalogQueryService {

    private static final Set<String> SORTABLE_PROPERTIES = Set.of("name", "price", "createdAt");

    private final CatalogQueryRepository repository;

    public CatalogQueryService(CatalogQueryRepository repository) {
        this.repository = repository;
    }

    public PageResponse<CatalogProductView> searchProducts(String query, String categorySlug, Pageable pageable) {
        AllowedSort.check(pageable, SORTABLE_PROPERTIES);
        return PageResponse.from(repository.searchProducts(
                SearchPatterns.contains(query), Strings.trimToNull(categorySlug), pageable));
    }

    public List<CatalogCategoryView> findCategories() {
        return repository.findCategoriesWithActiveProducts();
    }
}
