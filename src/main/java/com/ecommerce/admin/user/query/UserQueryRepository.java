package com.ecommerce.admin.user.query;

import com.ecommerce.admin.user.domain.User;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface UserQueryRepository extends Repository<User, Long> {

    String SELECT_ROW = """
            SELECT new com.ecommerce.admin.user.query.UserRow(
                u.id, u.email, u.firstName, u.lastName, u.enabled, u.createdAt, u.updatedAt)
            FROM User u
            """;

    String WHERE_FILTERS = """
             WHERE (LOWER(u.email) LIKE :pattern ESCAPE '!'
                    OR LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE :pattern ESCAPE '!')
               AND (:enabled IS NULL OR u.enabled = :enabled)
            """;

    @Query(value = SELECT_ROW + WHERE_FILTERS,
            countQuery = "SELECT COUNT(u) FROM User u " + WHERE_FILTERS)
    Page<UserRow> search(String pattern, Boolean enabled, Pageable pageable);

    @Query(SELECT_ROW + " WHERE u.id = :id")
    Optional<UserRow> findRowById(Long id);

    @Query("""
            SELECT new com.ecommerce.admin.user.query.UserRoleRow(u.id, r.id, r.name)
            FROM User u JOIN u.roles r
            WHERE u.id IN :userIds
            ORDER BY r.name
            """)
    List<UserRoleRow> findRoles(Collection<Long> userIds);

    @Query("""
            SELECT new com.ecommerce.admin.user.query.UserWarehouseRow(u.id, w.id, w.code, w.name)
            FROM User u JOIN u.warehouseIds warehouseId JOIN Warehouse w ON w.id = warehouseId
            WHERE u.id IN :userIds
            ORDER BY w.code
            """)
    List<UserWarehouseRow> findWarehouses(Collection<Long> userIds);
}
