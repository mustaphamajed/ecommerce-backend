package com.ecommerce.admin.user.query;

import com.ecommerce.admin.user.WarehouseSummary;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import com.ecommerce.admin.common.AllowedSort;
import com.ecommerce.admin.common.PageResponse;
import com.ecommerce.admin.common.SearchPatterns;
import com.ecommerce.admin.user.exception.UserNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserQueryService {

    private static final Set<String> SORTABLE_PROPERTIES =
            Set.of("email", "firstName", "lastName", "createdAt", "updatedAt");

    private final UserQueryRepository repository;

    public UserQueryService(UserQueryRepository repository) {
        this.repository = repository;
    }

    public PageResponse<UserView> search(String query, Boolean enabled, Pageable pageable) {
        AllowedSort.check(pageable, SORTABLE_PROPERTIES);
        Page<UserRow> rows = repository.search(SearchPatterns.contains(query), enabled, pageable);
        if (rows.isEmpty()) {
            return PageResponse.from(rows.map(row -> toView(row, Map.of(), Map.of())));
        }
        var userIds = rows.map(UserRow::id).toList();
        var roles = rolesByUser(userIds);
        var warehouses = warehousesByUser(userIds);
        return PageResponse.from(rows.map(row -> toView(row, roles, warehouses)));
    }

    public UserView findById(Long id) {
        var row = repository.findRowById(id).orElseThrow(() -> new UserNotFoundException(id));
        var userIds = List.of(id);
        return toView(row, rolesByUser(userIds), warehousesByUser(userIds));
    }

    private Map<Long, List<RoleSummary>> rolesByUser(List<Long> userIds) {
        return repository.findRoles(userIds).stream()
                .collect(groupingBy(UserRoleRow::userId,
                        mapping(row -> new RoleSummary(row.roleId(), row.roleName()), toList())));
    }

    private Map<Long, List<WarehouseSummary>> warehousesByUser(List<Long> userIds) {
        return repository.findWarehouses(userIds).stream()
                .collect(groupingBy(UserWarehouseRow::userId,
                        mapping(row -> new WarehouseSummary(row.warehouseId(), row.warehouseCode(),
                                row.warehouseName()), toList())));
    }

    private UserView toView(
            UserRow row, Map<Long, List<RoleSummary>> roles, Map<Long, List<WarehouseSummary>> warehouses) {
        return new UserView(
                row.id(),
                row.email(),
                row.firstName(),
                row.lastName(),
                row.enabled(),
                roles.getOrDefault(row.id(), List.of()),
                warehouses.getOrDefault(row.id(), List.of()),
                row.createdAt(),
                row.updatedAt());
    }
}
