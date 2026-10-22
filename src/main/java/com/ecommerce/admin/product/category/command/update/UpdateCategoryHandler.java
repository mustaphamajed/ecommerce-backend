package com.ecommerce.admin.product.category.command.update;

import com.ecommerce.admin.product.category.domain.CategoryRepository;
import com.ecommerce.admin.product.category.exception.DuplicateCategoryNameException;
import com.ecommerce.admin.product.category.exception.DuplicateCategorySlugException;
import com.ecommerce.admin.common.CommandHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateCategoryHandler implements CommandHandler<UpdateCategoryCommand, Void> {

    private final CategoryRepository repository;

    public UpdateCategoryHandler(CategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public Void handle(UpdateCategoryCommand command) {
        var category = repository.findByIdOrThrow(command.id());
        String slug = command.slug() != null ? command.slug() : category.getSlug();
        if (repository.existsByNameIgnoreCaseAndIdNot(command.name(), command.id())) {
            throw new DuplicateCategoryNameException(command.name());
        }
        if (repository.existsBySlugAndIdNot(slug, command.id())) {
            throw new DuplicateCategorySlugException(slug);
        }
        category.update(command.name(), slug, command.description());
        return null;
    }
}
