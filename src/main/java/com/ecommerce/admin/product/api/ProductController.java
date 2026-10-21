package com.ecommerce.admin.product.api;

import com.ecommerce.admin.common.PageResponse;
import com.ecommerce.admin.product.command.archive.ArchiveProductCommand;
import com.ecommerce.admin.product.command.archive.ArchiveProductHandler;
import com.ecommerce.admin.product.command.create.CreateProductCommand;
import com.ecommerce.admin.product.command.create.CreateProductHandler;
import com.ecommerce.admin.product.command.restore.RestoreProductCommand;
import com.ecommerce.admin.product.command.restore.RestoreProductHandler;
import com.ecommerce.admin.product.command.update.UpdateProductCommand;
import com.ecommerce.admin.product.command.update.UpdateProductHandler;
import com.ecommerce.admin.product.domain.ProductStatus;
import com.ecommerce.admin.product.query.ProductQueryService;
import com.ecommerce.admin.product.query.ProductView;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final CreateProductHandler createProduct;
    private final UpdateProductHandler updateProduct;
    private final ArchiveProductHandler archiveProduct;
    private final RestoreProductHandler restoreProduct;
    private final ProductQueryService queries;

    public ProductController(
            CreateProductHandler createProduct,
            UpdateProductHandler updateProduct,
            ArchiveProductHandler archiveProduct,
            RestoreProductHandler restoreProduct,
            ProductQueryService queries) {
        this.createProduct = createProduct;
        this.updateProduct = updateProduct;
        this.archiveProduct = archiveProduct;
        this.restoreProduct = restoreProduct;
        this.queries = queries;
    }

    @GetMapping
    public PageResponse<ProductView> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) ProductStatus status,
            @PageableDefault(size = 20, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return queries.search(q, categoryId, status, pageable);
    }

    @GetMapping("/{id}")
    public ProductView get(@PathVariable Long id) {
        return queries.findById(id);
    }

    @PostMapping
    public ResponseEntity<ProductView> create(@Valid @RequestBody ProductRequest request) {
        Long id = createProduct.handle(new CreateProductCommand(
                request.sku(), request.name(), request.description(), request.price(), request.categoryId()));
        return ResponseEntity.created(URI.create("/api/products/" + id))
                .body(queries.findById(id));
    }

    @PutMapping("/{id}")
    public ProductView update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        updateProduct.handle(new UpdateProductCommand(
                id, request.sku(), request.name(), request.description(), request.price(), request.categoryId()));
        return queries.findById(id);
    }

    @PostMapping("/{id}/archive")
    public ProductView archive(@PathVariable Long id) {
        archiveProduct.handle(new ArchiveProductCommand(id));
        return queries.findById(id);
    }

    @PostMapping("/{id}/restore")
    public ProductView restore(@PathVariable Long id) {
        restoreProduct.handle(new RestoreProductCommand(id));
        return queries.findById(id);
    }
}
