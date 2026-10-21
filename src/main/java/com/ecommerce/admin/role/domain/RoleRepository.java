package com.ecommerce.admin.role.domain;

import com.ecommerce.admin.role.exception.RoleNotFoundException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.id = :roleId")
    long countUsersWithRole(Long roleId);

    default Role findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new RoleNotFoundException(id));
    }
}
