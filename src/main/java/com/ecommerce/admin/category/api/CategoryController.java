package com.ecommerce.admin.category.api;

import com.ecommerce.admin.category.command.create.CreateCategoryCommand;
import com.ecommerce.admin.category.command.create.CreateCategoryHandler;
import com.ecommerce.admin.category.command.delete.DeleteCategoryCommand;
import com.ecommerce.admin.category.command.delete.DeleteCategoryHandler;
import com.ecommerce.admin.category.command.update.UpdateCategoryCommand;
import com.ecommerce.admin.category.command.update.UpdateCategoryHandler;
import com.ecommerce.admin.category.query.CategoryQueryService;
import com.ecommerce.admin.category.query.CategoryView;
import com.ecommerce.admin.common.PageResponse;
import com.ecommerce.admin.common.web.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Categories", description = "Product categories")
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CreateCategoryHandler createCategory;
    private final UpdateCategoryHandler updateCategory;
    private final DeleteCategoryHandler deleteCategory;
    private final CategoryQueryService queries;

    public CategoryController(
            CreateCategoryHandler createCategory,
            UpdateCategoryHandler updateCategory,
            DeleteCategoryHandler deleteCategory,
            CategoryQueryService queries) {
        this.createCategory = createCategory;
        this.updateCategory = updateCategory;
        this.deleteCategory = deleteCategory;
        this.queries = queries;
    }

    @GetMapping
    public PageResponse<CategoryView> list(
            @RequestParam(required = false) String q,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return queries.search(q, pageable);
    }

    @GetMapping("/{id}")
    public CategoryView get(@PathVariable Long id) {
        return queries.findById(id);
    }

    @GetMapping("/by-slug/{slug}")
    public CategoryView getBySlug(@PathVariable String slug) {
        return queries.findBySlug(slug);
    }

    @PostMapping
    public ResponseEntity<CategoryView> create(@Valid @RequestBody CategoryRequest request) {
        Long id = createCategory.handle(new CreateCategoryCommand(request.name(), request.description(), request.slug()));
        var category = queries.findById(id);
        return ApiResponses.created(
                URI.create("/api/categories/" + id), category, "Category " + category.name() + " created");
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryView> update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        updateCategory.handle(new UpdateCategoryCommand(id, request.name(), request.description(), request.slug()));
        var category = queries.findById(id);
        return ApiResponses.ok(category, "Category " + category.name() + " updated");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        String name = queries.findById(id).name();
        deleteCategory.handle(new DeleteCategoryCommand(id));
        return ApiResponses.noContent("Category " + name + " deleted");
    }
}
