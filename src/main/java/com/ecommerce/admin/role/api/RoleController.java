package com.ecommerce.admin.role.api;

import com.ecommerce.admin.common.web.ApiResponses;
import com.ecommerce.admin.role.command.create.CreateRoleCommand;
import com.ecommerce.admin.role.command.create.CreateRoleHandler;
import com.ecommerce.admin.role.command.delete.DeleteRoleCommand;
import com.ecommerce.admin.role.command.delete.DeleteRoleHandler;
import com.ecommerce.admin.role.command.update.UpdateRoleCommand;
import com.ecommerce.admin.role.command.update.UpdateRoleHandler;
import com.ecommerce.admin.role.query.RoleQueryService;
import com.ecommerce.admin.role.query.RoleView;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Roles", description = "Roles and their permissions")
@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final CreateRoleHandler createRole;
    private final UpdateRoleHandler updateRole;
    private final DeleteRoleHandler deleteRole;
    private final RoleQueryService queries;

    public RoleController(
            CreateRoleHandler createRole,
            UpdateRoleHandler updateRole,
            DeleteRoleHandler deleteRole,
            RoleQueryService queries) {
        this.createRole = createRole;
        this.updateRole = updateRole;
        this.deleteRole = deleteRole;
        this.queries = queries;
    }

    @GetMapping
    public List<RoleView> list() {
        return queries.findAll();
    }

    @GetMapping("/permissions")
    public List<String> permissions() {
        return queries.allPermissions();
    }

    @GetMapping("/{id}")
    public RoleView get(@PathVariable Long id) {
        return queries.findById(id);
    }

    @PostMapping
    public ResponseEntity<RoleView> create(@Valid @RequestBody RoleRequest request) {
        Long id = createRole.handle(new CreateRoleCommand(request.name(), request.description(), request.toPermissions()));
        var role = queries.findById(id);
        return ApiResponses.created(URI.create("/api/roles/" + id), role, "Role " + role.name() + " created");
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleView> update(@PathVariable Long id, @Valid @RequestBody RoleRequest request) {
        updateRole.handle(new UpdateRoleCommand(id, request.name(), request.description(), request.toPermissions()));
        var role = queries.findById(id);
        return ApiResponses.ok(role, "Role " + role.name() + " updated, users get the change at their next sign-in refresh");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        String name = queries.findById(id).name();
        deleteRole.handle(new DeleteRoleCommand(id));
        return ApiResponses.noContent("Role " + name + " deleted");
    }
}
