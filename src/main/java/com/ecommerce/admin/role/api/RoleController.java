package com.ecommerce.admin.role.api;

import com.ecommerce.admin.role.query.RoleQueryService;
import com.ecommerce.admin.role.query.RoleView;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleQueryService queries;

    public RoleController(RoleQueryService queries) {
        this.queries = queries;
    }

    @GetMapping
    public List<RoleView> list() {
        return queries.findAll();
    }
}
