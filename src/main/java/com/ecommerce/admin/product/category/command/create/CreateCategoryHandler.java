package com.ecommerce.admin.product.category.command.create;

import com.ecommerce.admin.product.category.domain.Category;
import com.ecommerce.admin.product.category.domain.CategoryRepository;
import com.ecommerce.admin.product.category.domain.CategorySlugs;
import com.ecommerce.admin.product.category.exception.DuplicateCategoryNameException;
import com.ecommerce.admin.product.category.exception.DuplicateCategorySlugException;
import com.ecommerce.admin.common.CommandHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateCategoryHandler implements CommandHandler<CreateCategoryCommand, Long> {

    private final CategoryRepository repository;

    public CreateCategoryHandler(CategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public Long handle(CreateCategoryCommand command) {
        String slug = command.slug() != null ? command.slug() : CategorySlugs.generateFrom(command.name());
        if (repository.existsByNameIgnoreCase(command.name())) {
            throw new DuplicateCategoryNameException(command.name());
        }
        if (repository.existsBySlug(slug)) {
            throw new DuplicateCategorySlugException(slug);
        }
        return repository.save(new Category(command.name(), slug, command.description())).getId();
    }
}
