package com.ecommerce.admin.role.api;

import com.ecommerce.admin.role.command.create.CreateRoleCommand;
import com.ecommerce.admin.role.command.create.CreateRoleHandler;
import com.ecommerce.admin.role.command.delete.DeleteRoleCommand;
import com.ecommerce.admin.role.command.delete.DeleteRoleHandler;
import com.ecommerce.admin.role.command.update.UpdateRoleCommand;
import com.ecommerce.admin.role.command.update.UpdateRoleHandler;
import com.ecommerce.admin.role.query.RoleQueryService;
import com.ecommerce.admin.role.query.RoleView;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
        return ResponseEntity.created(URI.create("/api/roles/" + id)).body(queries.findById(id));
    }

    @PutMapping("/{id}")
    public RoleView update(@PathVariable Long id, @Valid @RequestBody RoleRequest request) {
        updateRole.handle(new UpdateRoleCommand(id, request.name(), request.description(), request.toPermissions()));
        return queries.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        deleteRole.handle(new DeleteRoleCommand(id));
    }
}
