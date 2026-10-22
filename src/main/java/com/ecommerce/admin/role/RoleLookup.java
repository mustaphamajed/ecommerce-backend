package com.ecommerce.admin.role;

import com.ecommerce.admin.role.domain.Role;
import com.ecommerce.admin.role.domain.RoleRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RoleLookup {

    private final RoleRepository roles;

    public RoleLookup(RoleRepository roles) {
        this.roles = roles;
    }

    public List<Role> findAllById(Collection<Long> ids) {
        return roles.findAllById(ids);
    }

    public Optional<Role> findByName(String name) {
        return roles.findByName(name);
    }
}
