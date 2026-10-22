package com.ecommerce.admin.auth.query;

import com.ecommerce.admin.role.domain.Permission;
import com.ecommerce.admin.role.domain.Role;
import com.ecommerce.admin.user.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CurrentUserQueryService {

    private final UserRepository userRepository;

    public CurrentUserQueryService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public CurrentUserView findById(Long userId) {
        var user = userRepository.findByIdOrThrow(userId);
        return new CurrentUserView(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles().stream().map(Role::getName).sorted().toList(),
                user.getPermissions().stream().map(Permission::authority).sorted().toList(),
                user.getWarehouseIds().stream().sorted().toList());
    }
}
