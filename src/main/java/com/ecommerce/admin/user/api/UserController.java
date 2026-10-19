package com.ecommerce.admin.user.api;

import com.ecommerce.admin.common.PageResponse;
import com.ecommerce.admin.user.command.create.CreateUserCommand;
import com.ecommerce.admin.user.command.create.CreateUserHandler;
import com.ecommerce.admin.user.command.disable.DisableUserCommand;
import com.ecommerce.admin.user.command.disable.DisableUserHandler;
import com.ecommerce.admin.user.command.enable.EnableUserCommand;
import com.ecommerce.admin.user.command.enable.EnableUserHandler;
import com.ecommerce.admin.user.command.password.ResetUserPasswordCommand;
import com.ecommerce.admin.user.command.password.ResetUserPasswordHandler;
import com.ecommerce.admin.user.command.roles.AssignUserRolesCommand;
import com.ecommerce.admin.user.command.roles.AssignUserRolesHandler;
import com.ecommerce.admin.user.command.update.UpdateUserCommand;
import com.ecommerce.admin.user.command.update.UpdateUserHandler;
import com.ecommerce.admin.user.command.warehouses.AssignUserWarehousesCommand;
import com.ecommerce.admin.user.command.warehouses.AssignUserWarehousesHandler;
import com.ecommerce.admin.user.query.UserQueryService;
import com.ecommerce.admin.user.query.UserView;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final CreateUserHandler createUser;
    private final UpdateUserHandler updateUser;
    private final AssignUserRolesHandler assignRoles;
    private final AssignUserWarehousesHandler assignWarehouses;
    private final ResetUserPasswordHandler resetPassword;
    private final EnableUserHandler enableUser;
    private final DisableUserHandler disableUser;
    private final UserQueryService queries;

    public UserController(
            CreateUserHandler createUser,
            UpdateUserHandler updateUser,
            AssignUserRolesHandler assignRoles,
            AssignUserWarehousesHandler assignWarehouses,
            ResetUserPasswordHandler resetPassword,
            EnableUserHandler enableUser,
            DisableUserHandler disableUser,
            UserQueryService queries) {
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.assignRoles = assignRoles;
        this.assignWarehouses = assignWarehouses;
        this.resetPassword = resetPassword;
        this.enableUser = enableUser;
        this.disableUser = disableUser;
        this.queries = queries;
    }

    @GetMapping
    public PageResponse<UserView> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Boolean enabled,
            @PageableDefault(size = 20, sort = "email") Pageable pageable) {
        return queries.search(q, enabled, pageable);
    }

    @GetMapping("/{id}")
    public UserView get(@PathVariable Long id) {
        return queries.findById(id);
    }

    @PostMapping
    public ResponseEntity<UserView> create(@Valid @RequestBody CreateUserRequest request) {
        Long id = createUser.handle(new CreateUserCommand(
                request.email(),
                request.firstName(),
                request.lastName(),
                request.password(),
                request.roleIds(),
                request.warehouseIds()));
        return ResponseEntity.created(URI.create("/api/users/" + id))
                .body(queries.findById(id));
    }

    @PutMapping("/{id}")
    public UserView update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        updateUser.handle(new UpdateUserCommand(id, request.email(), request.firstName(), request.lastName()));
        return queries.findById(id);
    }

    @PutMapping("/{id}/roles")
    public UserView assignRoles(@PathVariable Long id, @Valid @RequestBody AssignRolesRequest request) {
        assignRoles.handle(new AssignUserRolesCommand(id, request.roleIds()));
        return queries.findById(id);
    }

    @PutMapping("/{id}/warehouses")
    public UserView assignWarehouses(@PathVariable Long id, @Valid @RequestBody AssignWarehousesRequest request) {
        assignWarehouses.handle(new AssignUserWarehousesCommand(id, request.warehouseIds()));
        return queries.findById(id);
    }

    @PutMapping("/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(@PathVariable Long id, @Valid @RequestBody ResetPasswordRequest request) {
        resetPassword.handle(new ResetUserPasswordCommand(id, request.password()));
    }

    @PostMapping("/{id}/enable")
    public UserView enable(@PathVariable Long id) {
        enableUser.handle(new EnableUserCommand(id));
        return queries.findById(id);
    }

    @PostMapping("/{id}/disable")
    public UserView disable(@PathVariable Long id) {
        disableUser.handle(new DisableUserCommand(id));
        return queries.findById(id);
    }
}
