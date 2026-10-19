package com.ecommerce.admin.user.command.warehouses;

import com.ecommerce.admin.common.CommandHandler;
import com.ecommerce.admin.user.domain.UserAssignments;
import com.ecommerce.admin.user.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AssignUserWarehousesHandler implements CommandHandler<AssignUserWarehousesCommand, Void> {

    private final UserRepository repository;
    private final UserAssignments assignments;

    public AssignUserWarehousesHandler(UserRepository repository, UserAssignments assignments) {
        this.repository = repository;
        this.assignments = assignments;
    }

    @Override
    public Void handle(AssignUserWarehousesCommand command) {
        var user = repository.findByIdOrThrow(command.userId());
        user.assignWarehouses(assignments.resolveWarehouses(command.warehouseIds()));
        return null;
    }
}
