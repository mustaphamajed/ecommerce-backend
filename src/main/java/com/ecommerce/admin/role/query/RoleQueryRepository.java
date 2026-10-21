package com.ecommerce.admin.role.query;

import com.ecommerce.admin.role.domain.Role;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface RoleQueryRepository extends Repository<Role, Long> {

    @Query("""
            SELECT new com.ecommerce.admin.role.query.RoleUserCount(r.id, COUNT(u))
            FROM User u JOIN u.roles r
            GROUP BY r.id
            """)
    List<RoleUserCount> countUsersPerRole();
}
