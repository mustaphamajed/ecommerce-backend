package com.ecommerce.admin.role.query;

import com.ecommerce.admin.role.domain.Permission;
import com.ecommerce.admin.role.domain.RoleRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RoleQueryService {

    private final RoleRepository roleRepository;
    private final RoleQueryRepository queryRepository;

    public RoleQueryService(RoleRepository roleRepository, RoleQueryRepository queryRepository) {
        this.roleRepository = roleRepository;
        this.queryRepository = queryRepository;
    }

    public List<RoleView> findAll() {
        Map<Long, Long> userCounts = queryRepository.countUsersPerRole().stream()
                .collect(Collectors.toMap(RoleUserCount::roleId, RoleUserCount::userCount));
        return roleRepository.findAll(Sort.by("name")).stream()
                .map(role -> RoleView.from(role, userCounts.getOrDefault(role.getId(), 0L)))
                .toList();
    }

    public RoleView findById(Long id) {
        var role = roleRepository.findByIdOrThrow(id);
        return RoleView.from(role, roleRepository.countUsersWithRole(id));
    }

    public List<String> allPermissions() {
        return Arrays.stream(Permission.values()).map(Permission::authority).toList();
    }
}
