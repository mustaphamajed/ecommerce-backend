package com.ecommerce.admin.user.domain;

import com.ecommerce.admin.user.exception.UserNotFoundException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    long countByEnabled(boolean enabled);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    @Query("""
            SELECT COUNT(u) FROM User u JOIN u.roles r
            WHERE r.name = :roleName AND u.enabled = true AND u.id <> :excludedUserId
            """)
    long countOtherEnabledUsersWithRole(String roleName, Long excludedUserId);

    default User findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }
}
