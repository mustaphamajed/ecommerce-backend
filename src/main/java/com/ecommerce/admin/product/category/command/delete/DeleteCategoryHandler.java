package com.ecommerce.admin.product.category.command.delete;

import com.ecommerce.admin.product.category.domain.CategoryRepository;
import com.ecommerce.admin.product.category.exception.CategoryInUseException;
import com.ecommerce.admin.common.CommandHandler;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeleteCategoryHandler implements CommandHandler<DeleteCategoryCommand, Void> {

    private final CategoryRepository repository;

    public DeleteCategoryHandler(CategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public Void handle(DeleteCategoryCommand command) {
        var category = repository.findByIdOrThrow(command.id());
        try {
            repository.delete(category);
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new CategoryInUseException(command.id());
        }
        return null;
    }
}
