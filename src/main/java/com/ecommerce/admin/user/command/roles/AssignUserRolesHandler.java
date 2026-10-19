package com.ecommerce.admin.user.command.roles;

import com.ecommerce.admin.common.CommandHandler;
import com.ecommerce.admin.role.domain.Role;
import com.ecommerce.admin.user.domain.LastAdminGuard;
import com.ecommerce.admin.user.domain.UserAssignments;
import com.ecommerce.admin.user.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AssignUserRolesHandler implements CommandHandler<AssignUserRolesCommand, Void> {

    private final UserRepository repository;
    private final UserAssignments assignments;
    private final LastAdminGuard lastAdminGuard;

    public AssignUserRolesHandler(
            UserRepository repository, UserAssignments assignments, LastAdminGuard lastAdminGuard) {
        this.repository = repository;
        this.assignments = assignments;
        this.lastAdminGuard = lastAdminGuard;
    }

    @Override
    public Void handle(AssignUserRolesCommand command) {
        var user = repository.findByIdOrThrow(command.userId());
        var roles = assignments.resolveRoles(command.roleIds());
        boolean keepsAdmin = roles.stream().anyMatch(role -> role.getName().equals(Role.ADMIN));
        if (!keepsAdmin) {
            lastAdminGuard.ensureAnotherAdminRemains(user);
        }
        user.assignRoles(roles);
        return null;
    }
}
