package com.ecommerce.admin.product.api;

import com.ecommerce.admin.common.PageResponse;
import com.ecommerce.admin.common.web.ApiResponses;
import com.ecommerce.admin.product.command.archive.ArchiveProductCommand;
import com.ecommerce.admin.product.command.archive.ArchiveProductHandler;
import com.ecommerce.admin.product.command.create.CreateProductCommand;
import com.ecommerce.admin.product.command.create.CreateProductHandler;
import com.ecommerce.admin.product.command.image.RemoveProductImageCommand;
import com.ecommerce.admin.product.command.image.RemoveProductImageHandler;
import com.ecommerce.admin.product.command.image.UploadProductImageCommand;
import com.ecommerce.admin.product.command.image.UploadProductImageHandler;
import com.ecommerce.admin.product.command.restore.RestoreProductCommand;
import com.ecommerce.admin.product.command.restore.RestoreProductHandler;
import com.ecommerce.admin.product.command.update.UpdateProductCommand;
import com.ecommerce.admin.product.command.update.UpdateProductHandler;
import com.ecommerce.admin.product.domain.ProductStatus;
import com.ecommerce.admin.product.query.ProductQueryService;
import com.ecommerce.admin.product.query.ProductView;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import java.net.URI;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Products", description = "Product catalog management, archive and restore")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final CreateProductHandler createProduct;
    private final UpdateProductHandler updateProduct;
    private final ArchiveProductHandler archiveProduct;
    private final RestoreProductHandler restoreProduct;
    private final UploadProductImageHandler uploadImage;
    private final RemoveProductImageHandler removeImage;
    private final ProductQueryService queries;

    public ProductController(
            CreateProductHandler createProduct,
            UpdateProductHandler updateProduct,
            ArchiveProductHandler archiveProduct,
            RestoreProductHandler restoreProduct,
            UploadProductImageHandler uploadImage,
            RemoveProductImageHandler removeImage,
            ProductQueryService queries) {
        this.createProduct = createProduct;
        this.updateProduct = updateProduct;
        this.archiveProduct = archiveProduct;
        this.restoreProduct = restoreProduct;
        this.uploadImage = uploadImage;
        this.removeImage = removeImage;
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
        var product = queries.findById(id);
        return ApiResponses.created(URI.create("/api/products/" + id), product, describe(product) + " created");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductView> update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        updateProduct.handle(new UpdateProductCommand(
                id, request.sku(), request.name(), request.description(), request.price(), request.categoryId()));
        var product = queries.findById(id);
        return ApiResponses.ok(product, describe(product) + " updated");
    }

    @PostMapping("/{id}/archive")
    public ResponseEntity<ProductView> archive(@PathVariable Long id) {
        archiveProduct.handle(new ArchiveProductCommand(id));
        var product = queries.findById(id);
        return ApiResponses.ok(product, describe(product) + " archived: it's hidden from the store");
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<ProductView> restore(@PathVariable Long id) {
        restoreProduct.handle(new RestoreProductCommand(id));
        var product = queries.findById(id);
        return ApiResponses.ok(product, describe(product) + " restored: it's back in the store");
    }

    @PutMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductView> uploadImage(@PathVariable Long id, @RequestPart("image") MultipartFile image)
            throws IOException {
        uploadImage.handle(new UploadProductImageCommand(id, image.getBytes()));
        var product = queries.findById(id);
        return ApiResponses.ok(product, "Image of " + describe(product) + " updated");
    }

    @DeleteMapping("/{id}/image")
    public ResponseEntity<ProductView> removeImage(@PathVariable Long id) {
        removeImage.handle(new RemoveProductImageCommand(id));
        var product = queries.findById(id);
        return ApiResponses.ok(product, "Image of " + describe(product) + " removed");
    }

    private static String describe(ProductView product) {
        return "Product " + product.sku();
    }
}
