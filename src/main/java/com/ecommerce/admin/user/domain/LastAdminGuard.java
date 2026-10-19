package com.ecommerce.admin.user.domain;

import com.ecommerce.admin.role.domain.Role;
import com.ecommerce.admin.user.exception.LastAdminException;
import org.springframework.stereotype.Component;

@Component
public class LastAdminGuard {

    private final UserRepository repository;

    public LastAdminGuard(UserRepository repository) {
        this.repository = repository;
    }

    public void ensureAnotherAdminRemains(User user) {
        boolean isActiveAdmin = user.isEnabled() && user.hasRole(Role.ADMIN);
        if (isActiveAdmin && repository.countOtherEnabledUsersWithRole(Role.ADMIN, user.getId()) == 0) {
            throw new LastAdminException();
        }
    }
}
