package com.ecommerce.admin.product.query;

import com.ecommerce.admin.common.AllowedSort;
import com.ecommerce.admin.common.PageResponse;
import com.ecommerce.admin.common.SearchPatterns;
import com.ecommerce.admin.product.domain.ProductStatus;
import com.ecommerce.admin.product.exception.ProductNotFoundException;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductQueryService {

    private static final Set<String> SORTABLE_PROPERTIES = Set.of("sku", "name", "price", "createdAt", "updatedAt");

    private final ProductQueryRepository repository;

    public ProductQueryService(ProductQueryRepository repository) {
        this.repository = repository;
    }

    public PageResponse<ProductView> search(String query, Long categoryId, ProductStatus status, Pageable pageable) {
        AllowedSort.check(pageable, SORTABLE_PROPERTIES);
        return PageResponse.from(repository.search(SearchPatterns.contains(query), categoryId, status, pageable));
    }

    public ProductView findById(Long id) {
        return repository.findViewById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }
}
