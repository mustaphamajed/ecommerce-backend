package com.ecommerce.admin.catalog.api;

import com.ecommerce.admin.catalog.query.CatalogCategoryView;
import com.ecommerce.admin.catalog.query.CatalogProductView;
import com.ecommerce.admin.catalog.query.CatalogQueryService;
import com.ecommerce.admin.common.PageResponse;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final CatalogQueryService queries;

    public CatalogController(CatalogQueryService queries) {
        this.queries = queries;
    }

    @GetMapping("/products")
    public PageResponse<CatalogProductView> products(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @PageableDefault(size = 12, sort = "name") Pageable pageable) {
        return queries.searchProducts(q, category, pageable);
    }

    @GetMapping("/categories")
    public List<CatalogCategoryView> categories() {
        return queries.findCategories();
    }
}
