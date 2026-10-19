package com.ecommerce.admin.role.query;

import com.ecommerce.admin.role.domain.RoleRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RoleQueryService {

    private final RoleRepository repository;

    public RoleQueryService(RoleRepository repository) {
        this.repository = repository;
    }

    public List<RoleView> findAll() {
        return repository.findAll(Sort.by("name")).stream()
                .map(RoleView::from)
                .toList();
    }
}
