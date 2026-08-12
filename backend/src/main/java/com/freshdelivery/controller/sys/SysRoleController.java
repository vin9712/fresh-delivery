package com.freshdelivery.controller.sys;

import com.freshdelivery.common.Result;
import com.freshdelivery.entity.sys.SysPermission;
import com.freshdelivery.entity.sys.SysRole;
import com.freshdelivery.service.sys.SysRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService roleService;

    @GetMapping("/list")
    public Result<List<SysRole>> list() {
        return Result.ok(roleService.list());
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody RoleForm form) {
        SysRole role = new SysRole();
        role.setRoleName(form.roleName());
        role.setRoleKey(form.roleKey());
        role.setDescription(form.description());
        roleService.create(role, form.permissionIds());
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody RoleForm form) {
        SysRole role = new SysRole();
        role.setRoleName(form.roleName());
        role.setRoleKey(form.roleKey());
        role.setDescription(form.description());
        roleService.update(id, role, form.permissionIds());
        return Result.ok();
    }

    @GetMapping("/{id}/permissions")
    public Result<List<SysPermission>> permissions(@PathVariable Long id) {
        return Result.ok(roleService.findRolePermissions(id));
    }

    public record RoleForm(
            String roleName, String roleKey, String description,
            List<Long> permissionIds) {}
}